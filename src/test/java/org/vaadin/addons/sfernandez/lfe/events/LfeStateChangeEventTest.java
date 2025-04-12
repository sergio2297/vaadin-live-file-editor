package org.vaadin.addons.sfernandez.lfe.events;

import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.LfeState;

import static org.assertj.core.api.Assertions.assertThat;

class LfeStateChangeEventTest {

    //---- Fixtures ----
    private LfeState dummyState() {
        return new LfeState();
    }

    //---- Tests ----
    @Test
    void createAssignsCorrectly_stateTest() {
        LfeState state = dummyState();

        LfeStateChangeEvent event = new LfeStateChangeEvent(state, null);

        assertThat(event.state()).isSameAs(state);
    }

    @Test
    void createAssignsCorrectly_oldStateTest() {
        LfeState oldState = dummyState();

        LfeStateChangeEvent event = new LfeStateChangeEvent(null, oldState);

        assertThat(event.oldState()).isSameAs(oldState);
    }

}