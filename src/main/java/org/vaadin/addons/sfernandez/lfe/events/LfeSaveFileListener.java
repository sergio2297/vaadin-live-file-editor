package org.vaadin.addons.sfernandez.lfe.events;

/**
 * <p>Listener that receives a {@link LfeSaveFileEvent}</p>
 */
@FunctionalInterface
public interface LfeSaveFileListener {

    /**
     * <p>Notifies when a save operation has been completed</p>
     * @param event the save event
     */
    void onSaveFile(final LfeSaveFileEvent event);

}
