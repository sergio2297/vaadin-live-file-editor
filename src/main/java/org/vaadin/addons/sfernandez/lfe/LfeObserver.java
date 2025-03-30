package org.vaadin.addons.sfernandez.lfe;

import com.vaadin.flow.shared.Registration;
import org.vaadin.addons.sfernandez.lfe.events.*;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link LiveFileEditor}'s component whose work is to store and notify listeners of all kinds related with the editor.
 */
public class LfeObserver {

    //---- Attributes ----
    private final List<LfeStateChangeListener> stateChangeListeners = new ArrayList<>();

    private final List<LfeWorkingStateChangeListener> workingStateChangeListeners = new ArrayList<>();
    private final List<LfeAutosaveWorkingStateChangeListener> autosaveWorkingStateChangeListeners = new ArrayList<>();

    private final List<LfeOpenFileListener> openFileListeners = new ArrayList<>();
    private final List<LfeCloseFileListener> closeFileListeners = new ArrayList<>();
    private final List<LfeSaveFileListener> saveFileListeners = new ArrayList<>();
    private final List<LfeAutosaveFileListener> autosaveFileListeners = new ArrayList<>();

    //---- Constructor ----
    LfeObserver() {}

    //---- Methods ----
    /**
     * <p>Add a new listener that will be notified every time the editor's state changes.</p>
     * @param listener Listener to add
     * @return the registration of the listener
     */
    public Registration addStateChangeListener(final LfeStateChangeListener listener) {
        return Registration.addAndRemove(stateChangeListeners, listener);
    }

    void notifyStateChangeEvent(final LfeStateChangeEvent event) {
        stateChangeListeners.forEach(listener -> listener.onStateChange(event));
    }

    /**
     * <p>Add a new listener that will be notified every time the editor starts/stops.</p>
     * @param listener Listener to add
     * @return the registration of the listener
     */
    public Registration addWorkingStateChangeListener(final LfeWorkingStateChangeListener listener) {
        return Registration.addAndRemove(workingStateChangeListeners, listener);
    }

    void notifyWorkingStateChangeEvent(final LfeWorkingStateChangeEvent event) {
        workingStateChangeListeners.forEach(listener -> listener.onWorkingStateChange(event));
    }

    /**
     * <p>Add a new listener that will be notified every time the autosave process starts/stops.</p>
     * @param listener Listener to add
     * @return the registration of the listener
     */
    public Registration addAutosaveWorkingStateChangeListener(final LfeAutosaveWorkingStateChangeListener listener) {
        return Registration.addAndRemove(autosaveWorkingStateChangeListeners, listener);
    }

    void notifyAutosaveWorkingStateChangeEvent(final LfeAutosaveWorkingStateChangeEvent event) {
        autosaveWorkingStateChangeListeners.forEach(listener -> listener.onAutosaveRunningStateChange(event));
    }

    /**
     * <p>Add a new listener that will be notified every time a file is opened.</p>
     * @param listener Listener to add
     * @return the registration of the listener
     */
    public Registration addOpenFileListener(final LfeOpenFileListener listener) {
        return Registration.addAndRemove(openFileListeners, listener);
    }

    void notifyOpenFileEvent(final LfeOpenFileEvent event) {
        openFileListeners.forEach(listener -> listener.onOpenFile(event));
    }

    /**
     * <p>Add a new listener that will be notified every time a file is closed.</p>
     * @param listener Listener to add
     * @return the registration of the listener
     */
    public Registration addCloseFileListener(final LfeCloseFileListener listener) {
        return Registration.addAndRemove(closeFileListeners, listener);
    }

    void notifyCloseFileEvent(final LfeCloseFileEvent event) {
        closeFileListeners.forEach(listener -> listener.onCloseFile(event));
    }

    /**
     * <p>Add a new listener that will be notified every time a save event occur.</p>
     * @param listener Listener to add
     * @return the registration of the listener
     */
    public Registration addSaveFileListener(final LfeSaveFileListener listener) {
        return Registration.addAndRemove(saveFileListeners, listener);
    }

    void notifySaveFileEvent(final LfeSaveFileEvent event) {
        saveFileListeners.forEach(listener -> listener.onSaveFile(event));
    }

    /**
     * <p>Add a new listener that will be notified every time an autosave event occur.</p>
     * @param listener Listener to add
     * @return the registration of the listener
     */
    public Registration addAutosaveFileListener(final LfeAutosaveFileListener listener) {
        return Registration.addAndRemove(autosaveFileListeners, listener);
    }

    void notifyAutosaveFileEvent(final LfeSaveFileEvent event) {
        autosaveFileListeners.forEach(listener -> listener.onAutosave(event));
    }

}
