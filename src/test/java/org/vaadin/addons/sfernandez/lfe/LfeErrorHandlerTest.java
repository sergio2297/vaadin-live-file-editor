package org.vaadin.addons.sfernandez.lfe;

import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.error.LfeException;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LfeErrorHandlerTest {

    //---- Constants and Definitions ----
    interface ExtractErrorOperation extends BiFunction<LfeErrorHandler, JsonValue, LfeError> {

        @Override
        default LfeError apply(LfeErrorHandler errorHandler, JsonValue json) {
            return extractErrorOf(errorHandler, json);
        }

        LfeError extractErrorOf(LfeErrorHandler errorHandler, JsonValue json);
    }

    //---- Attributes ----
    private final LfeErrorHandler errorHandler = new LfeErrorHandler();

    //---- Fixtures ----
    private static final JsonValue[] JSONS_THAT_ARENT_ERRORS = {
            Json.createArray(),
            Json.create("Error"),
            Json.create(false),
            Json.create(-1),
            Json.parse("{\"state\": \"failed\"}"),
            Json.parse("{\"error\": true}"),
            Json.parse("{\"error\": null}"),
            Json.parse("{\"error\": \"   \"}")
    };

    private static final String[] NOT_VALID_ERROR_MESSAGES = {
            "404",
            "true",
            "[\"error\",\"message\"]",
            "{\"context\":\"testing\"}",
    };

    public static Stream<Arguments> extractErrorOfOperations() {
        return Stream.of(
                Arguments.of((ExtractErrorOperation) LfeErrorHandler::creatingFileErrorOf, LfeError.Type.Creating.class),
                Arguments.of((ExtractErrorOperation) LfeErrorHandler::openingFileErrorOf, LfeError.Type.Opening.class),
                Arguments.of((ExtractErrorOperation) LfeErrorHandler::closingFileErrorOf, LfeError.Type.Closing.class),
                Arguments.of((ExtractErrorOperation) LfeErrorHandler::savingFileErrorOf, LfeError.Type.Saving.class)
        );
    }

    //---- Tests ----
    @Test
    void thereIsAnError_ifJsonIsNullTest() {
        boolean isError = errorHandler.thereIsAnError(null);

        assertThat(isError).isTrue();
    }

    @Test
    void thereIsNoError_ifJsonIsNotAnObjectContainingErrorPropertyProperlyTest() {
        for(JsonValue json : JSONS_THAT_ARENT_ERRORS) {
            boolean isError = errorHandler.thereIsAnError(json);

            assertThat(isError)
                    .withFailMessage(() -> "'" + json.toString() + "' expected to not be an error")
                    .isFalse();
        }
    }

    @Test
    void thereIsAnError_ifJsonContainsErrorProperty_withNotBlankValueTest() {
        JsonValue[] jsonValues = {
                Json.parse("{\"error\": \"UnknownError\"}"),
                Json.parse("{\"error\": \"TimeOutError\"}")
        };

        for(JsonValue json : jsonValues) {
            boolean isError = errorHandler.thereIsAnError(json);

            assertThat(isError)
                    .withFailMessage(() -> "'" + json.toString() + "' expected to be an error")
                    .isTrue();
        }
    }

    @ParameterizedTest
    @MethodSource("extractErrorOfOperations")
    void operationErrorOf_jsonThatDoesNotRepresentAnError_throwsExceptionTest(ExtractErrorOperation operation) {
        for(JsonValue json : JSONS_THAT_ARENT_ERRORS)
            assertThrows(LfeException.class, () -> operation.extractErrorOf(errorHandler, json));
    }

    @ParameterizedTest
    @MethodSource("extractErrorOfOperations")
    void operationErrorOf_jsonWithoutWellKnownType_returnErrorWithUnknownTypeTest(ExtractErrorOperation operation) {
        JsonObject json = Json.parse("{\"error\": \"UnrecognisedError\", \"message\": \"An strange error occurred\"}");

        LfeError error = operation.extractErrorOf(errorHandler, json);

        assertThat(error.type()).isEqualTo(LfeError.Type.Other.UNKNOWN);
    }

    @ParameterizedTest
    @MethodSource("extractErrorOfOperations")
    void operationErrorOf_jsonWithoutMessage_returnEmptyMessageTest(ExtractErrorOperation operation) {
        JsonObject json = Json.parse("{\"error\": \"UnrecognisedError\"}");

        LfeError error = operation.extractErrorOf(errorHandler, json);

        assertThat(error.message()).isBlank();
    }

    @ParameterizedTest
    @MethodSource("extractErrorOfOperations")
    void operationErrorOf_jsonWithNotValidMessage_returnErrorWithWhateverTheMessageWasTest(ExtractErrorOperation operation) {
        for(String message : NOT_VALID_ERROR_MESSAGES) {
            JsonObject json = Json.parse("{\"error\": \"UnrecognisedError\", \"message\": " + message + "}");

            LfeError error = operation.extractErrorOf(errorHandler, json);

            assertThat(error.message())
                    .withFailMessage(() -> "Message expected to be: '" + message + "' but was '" + error.message() + "'")
                    .isEqualTo(message);
        }
    }

    @ParameterizedTest
    @MethodSource("extractErrorOfOperations")
    void operationErrorOf_jsonWithWellKnownErrorType_returnErrorWithTypeTest(ExtractErrorOperation operation, Class<LfeError.Type> errorTypeClass) {
        for(LfeError.Type type : errorTypeClass.getEnumConstants()) {
            JsonObject json = Json.parse("{\"error\": \"" + type.code() + "\", \"message\": \"The user aborted a request\"}");

            LfeError error = operation.extractErrorOf(errorHandler, json);

            assertThat(error.type()).isEqualTo(type);
        }
    }

    @ParameterizedTest
    @MethodSource("extractErrorOfOperations")
    void operationErrorOf_jsonWithWellKnownErrorType_returnErrorWithMessageTest(ExtractErrorOperation operation, Class<LfeError.Type> errorTypeClass) {
        for(LfeError.Type type : errorTypeClass.getEnumConstants()) {
            String errorMessage = "The user aborted a request";
            JsonObject json = Json.parse("{\"error\": \"" + type.code() + "\", \"message\": \"" + errorMessage + "\"}");

            LfeError error = operation.extractErrorOf(errorHandler, json);

            assertThat(error.message()).isEqualTo(errorMessage);
        }
    }

}