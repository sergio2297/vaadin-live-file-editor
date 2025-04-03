package org.vaadin.addons.sfernandez.lfe.events;

/**
 * <p>Listener that receives a {@link LfeSaveFileEvent}</p>
 */
@FunctionalInterface
public interface LfeAutosaveFileListener {

    /**
     * <p>Notifies when an autosave operation has been completed</p>
     * @param event the autosave event
     */
    void onAutosave(final LfeSaveFileEvent event);

}
