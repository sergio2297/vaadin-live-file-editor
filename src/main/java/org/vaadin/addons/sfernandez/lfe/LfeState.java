package org.vaadin.addons.sfernandez.lfe;

import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class LfeState {

    //---- Attributes ----
    private final boolean editorIsWorking;
    private final boolean autosaveIsWorking;
    private final FileInfo openedFile;
    private final LocalDateTime lastSaveTime;
    private final String lastSaveData;

    //---- Constructor ----
    public LfeState() {
        this(false, false, null, null, null);
    }

    private LfeState(boolean editorIsWorking, boolean autosaveIsWorking, FileInfo openedFile, LocalDateTime lastSaveTime, String lastSaveData) {
        this.editorIsWorking = editorIsWorking;
        this.autosaveIsWorking = autosaveIsWorking;
        this.openedFile = openedFile;
        this.lastSaveTime = lastSaveTime;
        this.lastSaveData = lastSaveData;
    }

    //---- Methods ----
    public boolean editorIsWorking() {
        return editorIsWorking;
    }

    public boolean autosaveIsWorking() {
        return autosaveIsWorking;
    }

    public boolean thereIsFileOpened() {
        return openedFile != null;
    }

    public Optional<FileInfo> openedFile() {
        return Optional.ofNullable(openedFile);
    }

    public Optional<LocalDateTime> lastSaveTime() {
        return Optional.ofNullable(lastSaveTime);
    }

    public Optional<String> lastSaveData() {
        return Optional.ofNullable(lastSaveData);
    }

    public LfeState withEditorIsWorking(boolean editorIsWorking) {
        return new LfeState(
                editorIsWorking,
                autosaveIsWorking,
                openedFile,
                lastSaveTime,
                lastSaveData
        );
    }

    public LfeState withAutosaveIsWorking(boolean autosaveIsWorking) {
        return new LfeState(
                editorIsWorking,
                autosaveIsWorking,
                openedFile,
                lastSaveTime,
                lastSaveData
        );
    }

    public LfeState withOpenedFile(FileInfo openedFile) {
        return new LfeState(
                editorIsWorking,
                autosaveIsWorking,
                openedFile,
                lastSaveTime,
                lastSaveData
        );
    }

    public LfeState withLastSaveTime(LocalDateTime lastSaveTime) {
        return new LfeState(
                editorIsWorking,
                autosaveIsWorking,
                openedFile,
                lastSaveTime,
                lastSaveData
        );
    }

    public LfeState withLastSaveData(String lastSaveData) {
        return new LfeState(
                editorIsWorking,
                autosaveIsWorking,
                openedFile,
                lastSaveTime,
                lastSaveData
        );
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof LfeState lfeState)) return false;

        return editorIsWorking == lfeState.editorIsWorking
                && autosaveIsWorking == lfeState.autosaveIsWorking
                && Objects.equals(openedFile, lfeState.openedFile)
                && Objects.equals(lastSaveTime, lfeState.lastSaveTime)
                && Objects.equals(lastSaveData, lfeState.lastSaveData);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(editorIsWorking);
        result = 31 * result + Boolean.hashCode(autosaveIsWorking);
        result = 31 * result + Objects.hashCode(openedFile);
        result = 31 * result + Objects.hashCode(lastSaveTime);
        result = 31 * result + Objects.hashCode(lastSaveData);
        return result;
    }

    @Override
    public String toString() {
        return "LfeState{" +
                "editorIsWorking=" + editorIsWorking +
                ", autosaveIsWorking=" + autosaveIsWorking +
                ", openedFile=" + openedFile +
                ", lastSaveTime=" + lastSaveTime +
                ", lastSaveData='" + lastSaveData + '\'' +
                '}';
    }
}
