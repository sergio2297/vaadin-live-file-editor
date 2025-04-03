package org.vaadin.addons.sfernandez.lfe;

public record LfeState(
        boolean editorIsWorking,
        boolean thereIsFileOpened,
        boolean autosaveIsRunning
) {}
