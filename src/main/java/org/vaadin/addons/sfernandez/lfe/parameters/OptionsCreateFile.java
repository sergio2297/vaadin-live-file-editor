package org.vaadin.addons.sfernandez.lfe.parameters;

public final class OptionsCreateFile
        extends OptionsHandlingFilePicker {

    //---- Attributes ----
    private String suggestedName = null;

    //---- Methods ----
    public String getSuggestedName() {
        return suggestedName;
    }

    public void setSuggestedName(String suggestedName) {
        this.suggestedName = suggestedName;
    }
}
