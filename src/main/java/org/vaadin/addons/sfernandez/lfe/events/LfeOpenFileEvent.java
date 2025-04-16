package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.LiveFileEditor;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;

import java.util.Optional;

/**
 * <p>Event fired by {@link LiveFileEditor} each time an open operation is executed (successfully or not).</p>
 */
public class LfeOpenFileEvent
        implements LfeOperationEvent {

    //---- Attributes -----
    private final FileInfo fileInfo;
    private final LfeError error;

    //---- Constructor ----
    /**
     * <p>Creates a new LfeOpenFileEvent that represents an unsuccessful opening operation</p>
     * @param error Error that has occurred during the execution of the opening operation
     */
    public LfeOpenFileEvent(LfeError error) {
        this(null, error);
    }

    /**
     * <p>Creates a new LfeOpenFileEvent with the info of the opened file</p>
     * @param fileInfo info of the opened file
     */
    public LfeOpenFileEvent(FileInfo fileInfo) {
        this(fileInfo, null);
    }

    private LfeOpenFileEvent(FileInfo fileInfo, LfeError error) {
        this.fileInfo = fileInfo;
        this.error = error;
    }

    //---- Methods ----
    /**
     * @return return the opened file info
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
        return "LfeOpenFileEvent{" +
                "fileInfo=" + fileInfo +
                ", error=" + error +
                '}';
    }
}
