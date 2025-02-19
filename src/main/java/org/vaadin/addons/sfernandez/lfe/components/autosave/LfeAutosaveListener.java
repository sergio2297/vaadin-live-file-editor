package org.vaadin.addons.sfernandez.lfe.components.autosave;

/**
 * <p>Listener that receives a {@link LfeAutosaveEvent}</p>
 */
@FunctionalInterface
public interface LfeAutosaveListener {

    /**
     * <p>Notifies when an autosave operation has been completed</p>
     * @param event the autosave event
     */
    void onAutosave(final LfeAutosaveEvent event);

}
