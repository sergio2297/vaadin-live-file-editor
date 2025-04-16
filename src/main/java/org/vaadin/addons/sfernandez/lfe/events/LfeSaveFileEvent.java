package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.LfeAutosave;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;

import java.util.Optional;

/**
 * <p>Event fired by {@link LfeAutosave} each time an autosave operation is completed.</p>
 */
public class LfeSaveFileEvent
    implements LfeOperationEvent {

    //---- Attributes ----
    private final String data;
    private final LfeError error;

    //---- Constructor ----
    /**
     * <p>Creates a new LfeAutosaveEvent that has stored the data passed as argument</p>
     * @param data Data that has been saved
     */
    public LfeSaveFileEvent(String data) {
        this(data, null);
    }

    /**
     * <p>Creates a new LfeAutosaveEvent that has stored the data passed as argument</p>
     * @param data Data that has been saved (or not)
     * @param error Error that has occurred while the autosave was under execution
     */
    public LfeSaveFileEvent(String data, LfeError error) {
        this.data = data;
        this.error = error;
    }

    //---- Methods ----
    /**
     * @return the data tha has been saved (or not) by the autosave operation
     */
    public String data() {
        return data;
    }

    @Override
    public Optional<LfeError> error() {
        return Optional.ofNullable(error);
    }

    @Override
    public String toString() {
        return "LfeSaveFileEvent{" +
                "data='" + data + '\'' +
                ", error=" + error +
                '}';
    }
}
