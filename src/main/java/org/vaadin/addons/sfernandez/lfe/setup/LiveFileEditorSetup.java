package org.vaadin.addons.sfernandez.lfe.setup;

public class LiveFileEditorSetup {

    //---- Attributes ----
    private FileType[] allowedFileTypes = new FileType[0];

    //---- Methods ----
    public FileType[] getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(FileType... allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes == null
                ? new FileType[0]
                : allowedFileTypes;
    }

    public boolean isAllFileExtensionsAllowed() {
        return allowedFileTypes.length == 0;
    }

    public void allowAllFileExtensions() {
        this.allowedFileTypes = new FileType[0];
    }

}
