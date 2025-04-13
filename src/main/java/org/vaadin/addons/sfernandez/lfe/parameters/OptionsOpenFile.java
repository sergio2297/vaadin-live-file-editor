package org.vaadin.addons.sfernandez.lfe.parameters;

public final class OptionsOpenFile
        extends OptionsHandlingFilePicker {

    //---- Attributes ----
    private boolean multipleSelection = false;

    //---- Methods ----
    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public void setMultipleSelection(boolean multipleSelection) {
        this.multipleSelection = multipleSelection;
    }
}
