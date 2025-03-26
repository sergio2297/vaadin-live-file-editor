package org.vaadin.addons.sfernandez.lfe.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LfeWorkingStateChangeEventTest {

    @Test
    void createWithWorkingStateWorksTest() {
        LfeWorkingStateChangeEvent event = new LfeWorkingStateChangeEvent(false);

        assertThat(event.isWorking()).isFalse();
    }

}