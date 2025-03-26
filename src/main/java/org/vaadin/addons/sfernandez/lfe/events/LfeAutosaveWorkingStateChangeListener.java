package org.vaadin.addons.sfernandez.lfe.events;

/**
 * <p>Listener that receives a {@link LfeAutosaveWorkingStateChangeEvent}</p>
 */
@FunctionalInterface
public interface LfeAutosaveWorkingStateChangeListener {

    /**
     * <p>Notifies when the LfeAutosave's working state has changed</p>
     * @param event the working state changed event
     */
    void onAutosaveRunningStateChange(LfeAutosaveWorkingStateChangeEvent event);

}
