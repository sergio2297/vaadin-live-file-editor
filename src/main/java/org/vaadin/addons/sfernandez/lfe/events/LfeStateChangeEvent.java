package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.LfeState;
import org.vaadin.addons.sfernandez.lfe.LiveFileEditor;

/**
 * <p>Event fired by {@link LiveFileEditor} each time its state changes.</p>
 * @param state the current state
 * @param oldState the previous state
 */
public record LfeStateChangeEvent(LfeState state, LfeState oldState) {}
