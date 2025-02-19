package org.vaadin.addons.sfernandez.lfe.components.autosave;

public record LfeAutosaveEvent(boolean failed, String contentToSave) {
}
