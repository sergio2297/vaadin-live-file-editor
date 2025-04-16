package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.LiveFileEditor;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;

import java.util.Optional;

/**
 * <p>Event fired by {@link LiveFileEditor} each time a close operation is executed (successfully or not).</p>
 */
public class LfeCloseFileEvent
        implements LfeOperationEvent {

    //---- Attributes ----
    private final FileInfo fileInfo;
    private final LfeError error;
    
    //---- Constructor ----
    /**
     * <p>Creates a new LfeCloseFileEvent that represents an unsuccessful closing operation</p>
     * @param error Error that has occurred during the execution of the closing operation
     */
    public LfeCloseFileEvent(LfeError error) {
        this(null, error);
    }

    /**
     * <p>Creates a new LfeCloseFileEvent with the info of the closed file</p>
     * @param fileInfo info of the closed file
     */
    public LfeCloseFileEvent(FileInfo fileInfo) {
        this(fileInfo, null);
    }

    private LfeCloseFileEvent(FileInfo fileInfo, LfeError error) {
        this.fileInfo = fileInfo;
        this.error = error;
    }
    
    //---- Methods ----
    /**
     * @return return the closed file info
     */
    public Optional<FileInfo> fileInfo() {
        return Optional.ofNullable(fileInfo);
    }

    @Override
    public Optional<LfeError> error() {
        return Optional.ofNullable(error);
    }

    @Override
    public String toString() {
        return "LfeCloseFileEvent{" +
                "fileInfo=" + fileInfo +
                ", error=" + error +
                '}';
    }
}
