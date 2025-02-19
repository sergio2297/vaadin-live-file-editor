package org.vaadin.addons.sfernandez.lfe.components.autosave;

/**
 * <p>Event fired by {@link LfeAutosave} each time an autosave operation is completed.</p>
 * @param failed true if the autosave operation hasn't been completed successfully
 * @param data data that has been (or not) saved
 */
public record LfeAutosaveEvent(boolean failed, String data) {}
