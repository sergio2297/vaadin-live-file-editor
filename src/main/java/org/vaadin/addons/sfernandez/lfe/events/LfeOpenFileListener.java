package org.vaadin.addons.sfernandez.lfe.events;

/**
 * <p>Listener that receives a {@link LfeOpenFileEvent}</p>
 */
@FunctionalInterface
public interface LfeOpenFileListener {

    /**
     * <p>Notifies when open file operation has been completed</p>
     * @param event the open file event
     */
    void onOpenFile(LfeOpenFileEvent event);

}
