package org.vaadin.addons.sfernandez.lfe.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LfeErrorTest {

    //---- Fixtures ----
    private final LfeError sourceError = new LfeError(LfeError.Type.Other.UNKNOWN, "Unknown error");
    private final LfeOperationException exception = new LfeOperationException(sourceError);

    //---- Tests ----
    @Test
    void createFromException_assignsCorrectlyErrorTypeTest() {
        LfeError error = new LfeError(exception);

        assertThat(error.type()).isEqualTo(sourceError.type());
    }

    @Test
    void createFromException_assignsCorrectlyMessageTest() {
        LfeError error = new LfeError(exception);

        assertThat(error.message()).isEqualTo(sourceError.message());
    }

}