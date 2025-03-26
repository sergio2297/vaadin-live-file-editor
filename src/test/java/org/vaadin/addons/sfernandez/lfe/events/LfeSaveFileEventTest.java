package org.vaadin.addons.sfernandez.lfe.events;

import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;

import static org.assertj.core.api.Assertions.assertThat;

class LfeSaveFileEventTest
        implements LfeOperationEventTest {

    //---- Methods ----
    @Override
    public LfeOperationEvent createEventWithoutError() {
        return new LfeSaveFileEvent("Data that has been saved");
    }

    @Override
    public LfeOperationEvent createEventWithError(LfeError error) {
        return new LfeSaveFileEvent("Data to save", error);
    }

    //---- Tests ----
    @Test
    void createAssignsCorrectly_dataSavedTest() {
        LfeSaveFileEvent event = new LfeSaveFileEvent("Data to save");

        assertThat(event.data()).isEqualTo("Data to save");
    }

}