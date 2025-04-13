package org.vaadin.addons.sfernandez.lfe.parameters;

public class FileType {

    //---- Attributes ----
    private String description = "";
    private String mimeType;
    private String[] fileExtensions;

    //---- Constructor ----
    public FileType() {}

    public FileType(String description, String mimeType, String ... fileExtensions) {
        setDescription(description);
        setMimeType(mimeType);
        setFileExtensions(fileExtensions);
    }

    //---- Methods ----
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String[] getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(String[] fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

}
