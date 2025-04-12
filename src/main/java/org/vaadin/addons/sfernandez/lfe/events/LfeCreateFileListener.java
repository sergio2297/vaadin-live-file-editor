package org.vaadin.addons.sfernandez.lfe.events;

/**
 * <p>Listener that receives a {@link LfeCreateFileEvent}</p>
 */
@FunctionalInterface
public interface LfeCreateFileListener {

    /**
     * <p>Notifies when create file operation has been completed</p>
     * @param event the create file event
     */
    void onCreateFile(LfeCreateFileEvent event);

}
