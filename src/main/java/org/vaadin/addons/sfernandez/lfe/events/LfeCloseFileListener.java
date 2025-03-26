package org.vaadin.addons.sfernandez.lfe.events;

/**
 * <p>Listener that receives a {@link LfeCloseFileEvent}</p>
 */
@FunctionalInterface
public interface LfeCloseFileListener {

    /**
     * <p>Notifies when close file operation has been completed</p>
     * @param event the close file event
     */
    void onCloseFile(LfeCloseFileEvent event);

}
