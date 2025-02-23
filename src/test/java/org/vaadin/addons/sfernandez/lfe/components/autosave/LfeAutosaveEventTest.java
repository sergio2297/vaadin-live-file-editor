package org.vaadin.addons.sfernandez.lfe.components.autosave;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LfeAutosaveEventTest {

    @Test
    void createAssignsCorrectly_failedTest() {
        LfeAutosaveEvent event = new LfeAutosaveEvent(true, "Data to save");

        assertThat(event.failed()).isTrue();
    }

    @Test
    void createAssignsCorrectly_dataSavedTest() {
        LfeAutosaveEvent event = new LfeAutosaveEvent(true, "Data to save");

        assertThat(event.data()).isEqualTo("Data to save");
    }

}