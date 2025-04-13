package org.vaadin.addons.sfernandez.lfe.setup;

import org.vaadin.addons.sfernandez.lfe.parameters.FileType;

public class LfeSetup {

    //---- Attributes ----
    private boolean rememberLastDirectory = true;
    private FileType[] allowedFileTypes = new FileType[0];

    //---- Methods ----
    public boolean isRememberLastDirectory() {
        return rememberLastDirectory;
    }

    public void setRememberLastDirectory(boolean rememberOpenedDirectory) {
        this.rememberLastDirectory = rememberOpenedDirectory;
    }

    public FileType[] getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(FileType... allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes == null
                ? new FileType[0]
                : allowedFileTypes;
    }

    public boolean isAllFileTypesAllowed() {
        return allowedFileTypes.length == 0;
    }

    public void allowAllFileTypes() {
        this.allowedFileTypes = new FileType[0];
    }

}
