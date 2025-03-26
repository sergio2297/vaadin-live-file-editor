package org.vaadin.addons.sfernandez.lfe.events;

import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;

import static org.assertj.core.api.Assertions.assertThat;

public interface LfeOperationEventTest {

    //---- Fixtures ----
    private LfeError anError() {
        return new LfeError(LfeError.Type.Other.UNKNOWN, "Unknown error");
    }

    //---- Methods ----
    LfeOperationEvent createEventWithoutError();

    default LfeOperationEvent createEventWithNullError() {
        return createEventWithError(null);
    }

    LfeOperationEvent createEventWithError(LfeError error);

    //---- Tests ----
    @Test
    default void createWithoutAnError_isNotConsideredAsFailedTest() {
        LfeOperationEvent event = createEventWithoutError();

        assertThat(event.failed()).isFalse();
    }

    @Test
    default void createWithNullError_isNotConsideredAsFailedTest() {
        LfeOperationEvent event = createEventWithNullError();

        assertThat(event.failed()).isFalse();
    }

    @Test
    default void createWithNullError_returnEmptyErrorTest() {
        LfeOperationEvent event = createEventWithNullError();

        assertThat(event.error()).isEmpty();
    }

    @Test
    default void createWithError_isConsideredAsFailedTest() {
        LfeError error = anError();

        LfeOperationEvent event = createEventWithError(error);

        assertThat(event.failed()).isTrue();
    }

    @Test
    default void createWithError_returnErrorTest() {
        LfeError error = anError();

        LfeOperationEvent event = createEventWithError(error);

        assertThat(event.error()).isPresent();
        assertThat(event.error().orElseThrow()).isSameAs(error);
    }

}
