package org.vaadin.addons.sfernandez.lfe.error;

/**
 * <p>A {@link LfeError} keeps information about an error that occurred (almost always) during the execution of a
 * LiveFileEditor's operation like saving or opening a file.</p>
 * @param type Denotes the error type
 * @param message Message that gives more details about the error
 */
public record LfeError(Type type, String message) {

    //---- Constructor ----
    /**
     * <p>Creates a new LfeError from the information contained in the exception passed as argument</p>
     * @param exception Exception
     */
    public LfeError(LfeOperationException exception) {
        this(exception.getErrorType(), exception.getMessage());
    }

    /**
     * <p>Type of an LfeError</p>
     */
    public sealed interface Type {

        /**
         * @return the error code represented by a String
         */
        String code();

        enum Other implements Type {
            /** Special error type used to denote that an error is not recognized */
            UNKNOWN("UnknownError");

            private final String code;

            Other(String code) {
                this.code = code;
            }

            @Override
            public String code() {
                return code;
            }
        }

        /**
         * <p>Specific type error for Opening operation</p>
         */
        enum Opening implements Type {
            /** The user has closed intentionally the file selector aborting the operation of opening a file */
            ABORT("AbortError"),
            /** The system can't find the selected file during the operation of opening */
            NOT_FOUND("NotFoundError"),
            /** The user didn't grant the necessary permissions */
            PERMISSION_NOT_GRANTED("PermissionNotGrantedError");

            private final String code;

            Opening(final String code) {
                this.code = code;
            }

            public String code() {
                return code;
            }
        }

        /**
         * <p>Specific type error for Closing operation</p>
         */
        enum Closing implements Type {
            /** There is no file to close (probably any has been opened yet) */
            MISSING_FILE("MissingFileError");

            private final String code;

            Closing(final String code) {
                this.code = code;
            }

            public String code() {
                return code;
            }
        }

        /**
         * <p>Specific type error for Saving operation</p>
         */
        enum Saving implements Type {
            /** There is no file to save opened yet */
            MISSING_FILE("MissingFileError");

            private final String code;

            Saving(final String code) {
                this.code = code;
            }

            public String code() {
                return code;
            }
        }

    }

}
