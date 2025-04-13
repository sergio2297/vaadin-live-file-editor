package org.vaadin.addons.sfernandez.lfe.parameters;

public sealed abstract class OptionsHandlingFilePicker
        permits OptionsCreateFile, OptionsOpenFile {

    //---- Constants and Definitions ----
    public enum WellKnownDirectories {
        DESKTOP("desktop"),
        DOCUMENTS("documents"),
        DOWNLOADS("downloads"),
        MUSIC("music"),
        PICTURES("pictures"),
        VIDEOS("videos");

        private final String representation;

        WellKnownDirectories(String representation) {
            this.representation = representation;
        }

        public String getRepresentation() {
            return representation;
        }
    }

    //---- Attributes ----
    private boolean excludeAcceptAllOption = false;
    private WellKnownDirectories startIn = null;
    private String id;
    private FileType[] allowedFileTypes = new FileType[0];

    //---- Methods ----
    public boolean isExcludeAcceptAllOption() {
        return excludeAcceptAllOption;
    }

    public void setExcludeAcceptAllOption(boolean excludeAcceptAllOption) {
        this.excludeAcceptAllOption = excludeAcceptAllOption;
    }

    public WellKnownDirectories getStartIn() {
        return startIn;
    }

    public void setStartIn(WellKnownDirectories startIn) {
        this.startIn = startIn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(id != null && id.length() > 32)
            throw new IllegalArgumentException("Error. Id must not be larger than 32 characters");

        this.id = id;
    }

    public FileType[] getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(FileType[] allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }
}
