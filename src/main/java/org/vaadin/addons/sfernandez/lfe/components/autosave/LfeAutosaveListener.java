package org.vaadin.addons.sfernandez.lfe.components.autosave;

@FunctionalInterface
public interface LfeAutosaveListener {
    void onAutosave(final LfeAutosaveEvent event);
}
