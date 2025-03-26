package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.LfeAutosave;

/**
 * <p>Event fired by {@link LfeAutosave} each time it starts/stops working.</p>
 * @param isWorking true if the autosave process is working
 */
public record LfeAutosaveWorkingStateChangeEvent(boolean isWorking) {}
