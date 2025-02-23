package org.vaadin.addons.sfernandez.lfe.components.autosave;

import com.google.common.annotations.VisibleForTesting;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import org.vaadin.addons.sfernandez.lfe.LiveFileEditor;
import org.vaadin.addons.sfernandez.lfe.LiveFileEditorException;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;
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
    private final List<LfeAutosaveListener> autosaveListeners = new ArrayList<>();

    private final AutosaveProcess process = new AutosaveProcess();

    //---- Constructor ----
    /**
     * <p>Create a new {@link LfeAutosave} for the received editor</p>
     * @param editor {@link LiveFileEditor} to manage
     */
    public LfeAutosave(LiveFileEditor editor) {
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
    public boolean isRunning() {
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

        if(isRunning())
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

    /**
     * <p>Add a new listener that will be notified every time that an autosave event occur.</p>
     * @param listener Listener to add
     * @return the registration of the listener
     */
    public Registration addAutosaveListener(final LfeAutosaveListener listener) {
        return Registration.addAndRemove(autosaveListeners, listener);
    }

    private void fire(final LfeAutosaveEvent event) {
        autosaveListeners.forEach(listener -> listener.onAutosave(event));
    }

    @VisibleForTesting
    ExecutorService getExecutorService() {
        return executorService;
    }

    @VisibleForTesting
    CompletableFuture<Void> getSaveInProgress() {
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

        private CompletableFuture<Void> saveInProgress = null;
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

            scheduled = executorService.scheduleAtFixedRate(this::autosave, 0L, setup.frequency().toMillis(), TimeUnit.MILLISECONDS);
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

        public void stop() {
            undoChangesInUiPollInterval();
            reset();

            scheduled.cancel(true);
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

            ui.access(() -> {
                String contentToSave = getDataToSave();
                saveInProgress = editor.saveFile(contentToSave)
                        .thenAccept(fileSaved -> {
                            if(fileSaved)
                                previousDataSaved = contentToSave;

                            fire(new LfeAutosaveEvent(!fileSaved, contentToSave));
                        });
            });

            try {
                saveInProgress.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("This should never happen");
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
