package org.vaadin.addons.sfernandez.lfe;

import elemental.json.JsonObject;
import elemental.json.JsonValue;
import org.vaadin.addons.sfernandez.lfe.parameters.JsonParameterParser;
import org.vaadin.addons.sfernandez.lfe.setup.LiveFileEditorSetup;

class LfeJsParameterHandler {

    //---- Constants and Definitions ----

    //---- Attributes ----
    private final JsonParameterParser jsonParser = new JsonParameterParser();

    //---- Constructor ----

    //---- Methods ----
    public JsonValue transformToCreateFileRequest(final String suggestedName, final LiveFileEditorSetup setup) {
        JsonObject json = (JsonObject) jsonParser.asJson(setup.getAllowedFileTypes());

        if(suggestedName != null && !suggestedName.isBlank())
            json.put("suggestedName", suggestedName);

        return json;
    }

    public JsonValue transformToOpenFileRequest(final LiveFileEditorSetup setup) {
        return jsonParser.asJson(setup.getAllowedFileTypes());
    }

}
