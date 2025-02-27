package org.vaadin.addons.sfernandez.lfe.components.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LfeOperationExceptionTest {

    //---- Tests ----
    @SuppressWarnings({"DataFlowIssue", "ThrowableNotThrown"})
    @Test
    void create_withNullError_throwNullPointerExceptionTest() {
        assertThrows(NullPointerException.class, () -> new LfeOperationException(null));
    }

    @Test
    void messageOfException_isTakenFromLfeErrorTest() {
        LfeError error = new LfeError(LfeError.Type.Other.UNKNOWN, "Some error message");

        LfeOperationException exception = new LfeOperationException(error);

        assertThat(exception.getMessage()).isEqualTo(error.message());
    }

    @Test
    void typeErrorOfException_isTakenFromLfeErrorTest() {
        LfeError error = new LfeError(LfeError.Type.Other.UNKNOWN, "Some error message");

        LfeOperationException exception = new LfeOperationException(error);

        assertThat(exception.getErrorType()).isEqualTo(error.type());
    }

}