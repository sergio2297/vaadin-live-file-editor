package org.vaadin.addons.sfernandez.lfe;

import elemental.json.JsonObject;
import elemental.json.JsonString;
import elemental.json.JsonValue;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.error.LfeException;

import java.util.Optional;

class LfeErrorHandler {

    //---- Methods ----
    /**
     * @param json JsonValue to evaluate if it contains an error or not
     * @return true if the json is null or, it's a Json Object with a 'error' property with a not blank string value
     */
    public boolean thereIsAnError(JsonValue json) {
        if(json == null)
            return true;

        if(!(json instanceof JsonObject object))
            return false;

        return object.hasKey("error")
                && (object.get("error") instanceof JsonString errorCode)
                && !errorCode.getString().isBlank();
    }

    /**
     * <p>Extract an {@link LfeError} of type {@link LfeError.Type.Creating} from the given json.</p>
     * @param json Json which contains an {@link LfeError.Type.Creating} error
     * @return a {@link LfeError} of type {@link LfeError.Type.Creating} or {@link LfeError.Type.Other#UNKNOWN} if the
     * error code isn't recognized
     * @throws LfeException if no error is in the Json {@link #thereIsAnError(JsonValue)}
     */
    public LfeError creatingFileErrorOf(JsonValue json) {
        assertThereIsAnErrorIn(json);

        return new LfeError(
                getErrorPropertyOf(json, LfeError.Type.Creating.class),
                getMessagePropertyOf(json)
        );
    }

    /**
     * <p>Extract an {@link LfeError} of type {@link LfeError.Type.Opening} from the given json.</p>
     * @param json Json which contains an {@link LfeError.Type.Opening} error
     * @return a {@link LfeError} of type {@link LfeError.Type.Opening} or {@link LfeError.Type.Other#UNKNOWN} if the
     * error code isn't recognized
     * @throws LfeException if no error is in the Json {@link #thereIsAnError(JsonValue)}
     */
    public LfeError openingFileErrorOf(JsonValue json) {
        assertThereIsAnErrorIn(json);

        return new LfeError(
                getErrorPropertyOf(json, LfeError.Type.Opening.class),
                getMessagePropertyOf(json)
        );
    }

    /**
     * <p>Extract an {@link LfeError} of type {@link LfeError.Type.Closing} from the given json.</p>
     * @param json Json which contains an {@link LfeError.Type.Closing} error
     * @return a {@link LfeError} of type {@link LfeError.Type.Closing} or {@link LfeError.Type.Other#UNKNOWN} if the
     * error code isn't recognized
     * @throws LfeException if no error is in the Json {@link #thereIsAnError(JsonValue)}
     */
    public LfeError closingFileErrorOf(JsonValue json) {
        assertThereIsAnErrorIn(json);

        return new LfeError(
                getErrorPropertyOf(json, LfeError.Type.Closing.class),
                getMessagePropertyOf(json)
        );
    }

    /**
     * <p>Extract an {@link LfeError} of type {@link LfeError.Type.Saving} from the given json.</p>
     * @param json Json which contains an {@link LfeError.Type.Saving} error
     * @return a {@link LfeError} of type {@link LfeError.Type.Saving} or {@link LfeError.Type.Other#UNKNOWN} if the
     * error code isn't recognized
     * @throws LfeException if no error is in the Json {@link #thereIsAnError(JsonValue)}
     */
    public LfeError savingFileErrorOf(JsonValue json) {
        assertThereIsAnErrorIn(json);

        return new LfeError(
                getErrorPropertyOf(json, LfeError.Type.Saving.class),
                getMessagePropertyOf(json)
        );
    }

    private void assertThereIsAnErrorIn(JsonValue json) {
        if(thereIsAnError(json))
            return;

        throw new LfeException("Error. '" + json + "' does not represent an error.");
    }

    private <TYPE_ERROR extends Enum<TYPE_ERROR> & LfeError.Type>
    LfeError.Type getErrorPropertyOf(JsonValue json, Class<TYPE_ERROR> errorType) {
        String error = ((JsonObject) json).getString("error");

        return searchErrorTypeByName(error, errorType).orElse(LfeError.Type.Other.UNKNOWN);
    }

    private <TYPE_ERROR extends Enum<TYPE_ERROR> & LfeError.Type>
    Optional<LfeError.Type> searchErrorTypeByName(final String error, final Class<TYPE_ERROR> enumClass) {
        for(TYPE_ERROR type : enumClass.getEnumConstants())
            if(type.code().equalsIgnoreCase(error))
                return Optional.of(type);

        return Optional.empty();
    }

    private String getMessagePropertyOf(JsonValue json) {
        if(!((JsonObject) json).hasKey("message"))
            return "";

        return Optional.<JsonValue>ofNullable(((JsonObject) json).get("message"))
                .map(jsonValue -> {
                    if(!(jsonValue instanceof JsonString))
                        return jsonValue.toJson();
                    else
                        return jsonValue.asString();
                })
                .orElse("");
    }

}
