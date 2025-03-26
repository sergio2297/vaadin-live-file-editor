package org.vaadin.addons.sfernandez.lfe.events;

/**
 * <p>Listener that receives a {@link LfeWorkingStateChangeEvent}</p>
 */
@FunctionalInterface
public interface LfeWorkingStateChangeListener {

    /**
     * <p>Notifies when the Lfe's working state has changed</p>
     * @param event the working state changed event
     */
    void onWorkingStateChange(LfeWorkingStateChangeEvent event);

}
