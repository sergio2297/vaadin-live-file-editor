package org.vaadin.addons.sfernandez.lfe.events;

import org.vaadin.addons.sfernandez.lfe.error.LfeError;

import java.util.Optional;

/**
 * <p>Generic event used for LfeOperations</p>
 */
public interface LfeOperationEvent {

    /**
     * @return an optional with the {@link LfeError} result of the executing an operation
     */
    Optional<LfeError> error();

    /**
     * @return true if the operation executed failed
     */
    default boolean failed() {
        return error().isPresent();
    }

}
