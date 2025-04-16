package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.LiveFileEditor;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;

import java.util.Optional;

/**
 * <p>Event fired by {@link LiveFileEditor} each time a create file operation is executed (successfully or not).</p>
 */
public class LfeCreateFileEvent
        implements LfeOperationEvent {

    //---- Attributes -----
    private final FileInfo fileInfo;
    private final LfeError error;

    //---- Constructor ----
    /**
     * <p>Creates a new LfeCreateFileEvent that represents an unsuccessful creation</p>
     * @param error Error that has occurred during the execution of the creation
     */
    public LfeCreateFileEvent(LfeError error) {
        this(null, error);
    }

    /**
     * <p>Creates a new LfeCreateFileEvent with the info of the created file</p>
     * @param fileInfo info of the created file
     */
    public LfeCreateFileEvent(FileInfo fileInfo) {
        this(fileInfo, null);
    }

    private LfeCreateFileEvent(FileInfo fileInfo, LfeError error) {
        this.fileInfo = fileInfo;
        this.error = error;
    }

    //---- Methods ----
    /**
     * @return return the created file info
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
        return "LfeCreateFileEvent{" +
                "fileInfo=" + fileInfo +
                ", error=" + error +
                '}';
    }
}
