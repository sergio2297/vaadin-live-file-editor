package org.vaadin.addons.sfernandez.lfe;

import com.google.common.annotations.VisibleForTesting;
import com.vaadin.flow.component.UI;
import org.vaadin.addons.sfernandez.lfe.error.LiveFileEditorException;
import org.vaadin.addons.sfernandez.lfe.events.LfeAutosaveWorkingStateChangeEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeSaveFileEvent;
import org.vaadin.addons.sfernandez.lfe.setup.LfeAutosaveSetup;

import java.lang.ref.Cleaner;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * <p>{@link LiveFileEditor}'s component used to manage the autosave process.</p>
 */
public final class LfeAutosave {

    //---- Attributes ----
    private final LiveFileEditor editor;

    private boolean isEnabled = false;
    private LfeAutosaveSetup setup = null;

    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private final AutosaveProcess process = new AutosaveProcess();

    //---- Constructor ----
    /**
     * <p>Create a new {@link LfeAutosave} for the received editor</p>
     * @param editor {@link LiveFileEditor} to manage
     */
    LfeAutosave(LiveFileEditor editor) {
        this.editor = editor;

        configureCleanUpResources();
    }

    private void configureCleanUpResources() {
        Cleaner.create().register(this, executorService::shutdownNow);
    }

    //---- Methods ----
    /**
     * <p>Enables the autosave process. Autosave must be enabled to work after calling {@link #start()}</p>
     * <p>Default: false</p>
     * @param enable true to enable autosave
     */
    public void setEnabled(boolean enable) {
        this.isEnabled = enable;
    }

    /**
     * @return true if the autosave process is enable
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * <p>Establish the setup to use. Must not be null.</p>
     * <p>It is necessary to configure the autosave before starting it, if not, an exception will be thrown.</p>
     * @param setup Setup
     */
    public void setup(LfeAutosaveSetup setup) {
        this.setup = setup;
    }

    /**
     * @return true if the autosave process is running. To achieve this it's necessary to {@link #setEnabled(boolean)} and
     * call {@link #start()} previously and in that order
     */
    public boolean isWorking() {
        return process.isRunning();
    }

    /**
     * <p>Start the autosave process.</p>
     * <p>If the autosave is not enabled, then the process will not start. No exception is thrown.</p>
     * <p>Depending on the {@link LfeAutosaveSetup} the UI poll interval will be altered to be enough to execute the
     * autosave process properly. When the autosave process is stopped then the poll interval will be restored.</p>
     * <p>If it is already running, then the current process is stopped before starting a new one.</p>
     * @throws LiveFileEditorException if no setup has been added or if the attached editor {@link LiveFileEditor#isNotWorking()}
     */
    public void start() {
        if(!isEnabled)
            return;

        if(setup == null)
            throw new LiveFileEditorException("Error. It's necessary to setup the autosave before stating it.");

        if(editor.isNotWorking())
            throw new LiveFileEditorException("Error. Editor must be working before starting autosave.");

        if(isWorking())
            stop();

        process.start();
    }

    /**
     * <p>Stop the autosave process.</p>
     * <p>If the process isn't running, nothing happens.</p>
     * <p>Depending on the {@link LfeAutosaveSetup} the UI poll interval will be restored or not after stopping the process.</p>
     */
    public void stop() {
        process.stop();
    }

    private void notifyWorkingStateChanged() {
        editor.observer().notifyAutosaveWorkingStateChangeEvent(new LfeAutosaveWorkingStateChangeEvent(isWorking()));
    }

    private void fire(final LfeSaveFileEvent event) {
        editor.observer().notifyAutosaveFileEvent(event);
    }

    @VisibleForTesting
    ExecutorService getExecutorService() {
        return executorService;
    }

    @VisibleForTesting
    CompletableFuture<Optional<String>> getSaveInProgress() {
        return process.saveInProgress;
    }

    /* ***************************************
     *          AUTOSAVE PROCESS
     * **************************************/
    private class AutosaveProcess {

        //---- Attributes ----
        private ScheduledFuture<?> scheduled = null;

        private UI ui = null;
        private int previousUiPollInterval = -1;

        private CompletableFuture<Optional<String>> saveInProgress;
        private String previousDataSaved = null;

        //---- Constructor ----
        public AutosaveProcess() {}

        //---- Methods ----
        public boolean isRunning() {
            return scheduled != null && !scheduled.isDone();
        }

        public void start() {
            catchEditorAttachedUi();
            ensureSufficientUiPollInterval();

            scheduled = executorService.scheduleAtFixedRate(this::routine, 0L, setup.frequency().toMillis(), TimeUnit.MILLISECONDS);
            notifyWorkingStateChanged();
        }

        private void catchEditorAttachedUi() {
            Optional<UI> ui = editor.getAttachment().getUI();
            if(ui.isEmpty())
                throw new LiveFileEditorException("Error. It's not possible to autosave content due to the editor isn't attached to an UI.");

            this.ui = ui.get();
        }

        private void ensureSufficientUiPollInterval() {
            int autosaveFrequency = (int) setup.frequency().toMillis();
            int uiPollInterval = ui.getPollInterval();

            if(!setup.isAllowedToAlterUiPollInterval() && (uiPollInterval == -1 || uiPollInterval < autosaveFrequency)) {
                System.out.println("Warning! Ui poll interval is disabled or is larger than the autosave process frequency. This could end causing outdated saves.");
                return;
            }

            previousUiPollInterval = uiPollInterval;
            if(previousUiPollInterval == -1 || previousUiPollInterval < autosaveFrequency)
                ui.setPollInterval(autosaveFrequency);
        }

        private void routine() {
            try {
                autosave();
            } catch (Exception e) {
                start();
            }
        }

        public void stop() {
            undoChangesInUiPollInterval();
            reset();

            scheduled.cancel(true);
            notifyWorkingStateChanged();
        }

        private void undoChangesInUiPollInterval() {
            if(!setup.isAllowedToAlterUiPollInterval())
                return;

            ui.setPollInterval(previousUiPollInterval);
        }

        private void reset() {
            ui = null;
            previousUiPollInterval = -1;

            if(saveInProgress != null)
                saveInProgress.cancel(true);
            saveInProgress = null;
            previousDataSaved = null;
        }

        public synchronized void autosave() {
            if(autosaveIsNotNecessary())
                return;

            try {
                ui.access(() ->
                        saveInProgress = editor.saveFile(getDataToSave())
                ).get(ui.getPollInterval(), TimeUnit.MILLISECONDS);

                saveInProgress.get().ifPresent(savedContent -> {
                    previousDataSaved = savedContent;
                    fire(new LfeSaveFileEvent(savedContent));
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException("This should never happen");
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

        private boolean autosaveIsNotNecessary() {
            return dataToSaveHasNotChanged();
        }

        private boolean dataToSaveHasNotChanged() {
            return previousDataSaved != null && previousDataSaved.equals(getDataToSave());
        }

        private String getDataToSave() {
            return setup.dataToSave().get();
        }

    }
}
