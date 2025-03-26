package org.vaadin.addons.sfernandez.lfe.error;

import org.vaadin.addons.sfernandez.lfe.LiveFileEditor;

import java.util.concurrent.CompletableFuture;

/**
 * <p>A special type of RuntimeException used by the {@link LiveFileEditor} to complete exceptionally the {@link CompletableFuture}
 * returned after executing any of its async operations. E.g. save or open a file</p>
 */
public class LfeOperationException
        extends RuntimeException {

    //---- Attributes ----
    private final LfeError.Type errorType;

    //---- Constructor ----
    /**
     * <p>Creates a new LfeOperationException from the LfeError received as argument.</p>
     * @param error LfeError that occurred during the operation
     */
    public LfeOperationException(final LfeError error) {
        super(error.message());
        this.errorType = error.type();
    }

    //---- Methods ----
    /**
     * @return the error type that causes this exception
     */
    public LfeError.Type getErrorType() {
        return errorType;
    }

}
