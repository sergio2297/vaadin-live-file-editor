package org.vaadin.addons.sfernandez.lfe.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LfeAutosaveWorkingStateChangeEventTest {

    @Test
    void createWithWorkingStateWorksTest() {
        LfeAutosaveWorkingStateChangeEvent event = new LfeAutosaveWorkingStateChangeEvent(false);

        assertThat(event.isWorking()).isFalse();
    }

}