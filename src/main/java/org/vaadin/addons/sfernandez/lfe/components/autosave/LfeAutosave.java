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

public final class LfeAutosave {

    //---- Attributes ----
    private final LiveFileEditor editor;

    private boolean isEnabled = false;
    private LfeAutosaveSetup setup = null;

    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    private final List<LfeAutosaveListener> autosaveListeners = new ArrayList<>();

    private final AutosaveProcess process = new AutosaveProcess();

    //---- Constructor ----
    public LfeAutosave(LiveFileEditor editor) {
        this.editor = editor;

        configureCleanUpResources();
    }

    private void configureCleanUpResources() {
        Cleaner.create().register(this, executorService::shutdownNow);
    }

    //---- Methods ----
    public void setEnabled(boolean enable) {
        this.isEnabled = enable;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setup(LfeAutosaveSetup setup) {
        this.setup = setup;
    }

    public boolean isRunning() {
        return process.isRunning();
    }

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

    public void stop() {
        process.stop();
    }

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

    /* ***************************************
     *          AUTOSAVE PROCESS
     * **************************************/
    class AutosaveProcess {

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

            if(!setup.isAllowedToAlterUiPollInterval() && (uiPollInterval == -1 || uiPollInterval > autosaveFrequency)) {
                System.out.println("Warning! Ui poll interval is disabled or is larger than the autosave process frequency. This could end causing outdated saves.");
                return;
            }

            previousUiPollInterval = uiPollInterval;
            if(previousUiPollInterval == -1 || previousUiPollInterval > autosaveFrequency)
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

            saveInProgress.complete(null);
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
                            saveInProgress.complete(null);

                            if(fileSaved)
                                previousDataSaved = contentToSave;

                            fire(new LfeAutosaveEvent(!fileSaved, contentToSave));
                        });
            });

            try {
                saveInProgress.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
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
