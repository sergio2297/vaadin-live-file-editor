package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.LiveFileEditor;

/**
 * <p>Event fired by {@link LiveFileEditor} each time it starts/stops working.</p>
 * @param isWorking true if the editor is working
 */
public record LfeWorkingStateChangeEvent(boolean isWorking) {}
