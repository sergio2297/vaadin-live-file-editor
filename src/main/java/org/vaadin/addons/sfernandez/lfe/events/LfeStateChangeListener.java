package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.LfeState;

/**
 * <p>Listener that receives a {@link LfeStateChangeEvent}</p>
 */
@FunctionalInterface
public interface LfeStateChangeListener {

    /**
     * <p>Notifies when the {@link LfeState} has changed</p>
     * @param event the state changed event
     */
    void onStateChange(LfeStateChangeEvent event);

}
