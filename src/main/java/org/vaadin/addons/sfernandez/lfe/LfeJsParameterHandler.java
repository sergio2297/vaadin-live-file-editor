package org.vaadin.addons.sfernandez.lfe;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import es.sfernandez.library4j.types.DataSize;
import org.vaadin.addons.sfernandez.lfe.error.LfeException;
import org.vaadin.addons.sfernandez.lfe.parameters.*;

class LfeJsParameterHandler {

    //---- Attributes ----
    private final JsonParameterParser jsonParser = new JsonParameterParser();

    //---- Methods ----
    public JsonValue mapToJson(OptionsCreateFile options) {
        JsonObject json = Json.createObject();

        mapJsonProperties(json, options);

        if(options.getSuggestedName() != null && !options.getSuggestedName().isBlank())
            json.put("suggestedName", options.getSuggestedName());

        return json;
    }

    public JsonValue mapToJson(OptionsOpenFile options) {
        JsonObject json = Json.createObject();

        mapJsonProperties(json, options);

        json.put("multiple", options.isMultipleSelection());

        return json;
    }

    private void mapJsonProperties(JsonObject json, OptionsHandlingFilePicker options) {
        json.put("excludeAcceptAllOption", options.isExcludeAcceptAllOption());

        if(options.getStartIn() != null)
            json.put("startIn", options.getStartIn().getRepresentation());

        if(options.getId() != null)
            json.put("id", options.getId());

        if(options.getAllowedFileTypes() != null && options.getAllowedFileTypes().length > 0)
            json.put("types", jsonParser.asJson(options.getAllowedFileTypes()));
    }

    public FileInfo mapToFileInfo(JsonValue json) {
        return jsonParser.toFileInfo(json);
    }

    /* **************************
     *      JsonParameter Parser
     * *************************/
    private static class JsonParameterParser {

        //---- Methods ----
        private JsonValue asJson(FileType[] fileTypes) {
            if(fileTypes == null)
                return Json.createArray();

            JsonArray array = Json.createArray();

            for(int i = 0; i < fileTypes.length; ++i)
                if(fileTypes[i] != null)
                    array.set(i, asJson(fileTypes[i]));

            return array;
        }

        private JsonValue asJson(FileType fileType) {
            if(fileType.getMimeType() == null || fileType.getMimeType().isBlank())
                throw new LfeException("Error. MIME type can't be null");

            JsonObject object = Json.createObject();

            if(fileType.getDescription() != null && !fileType.getDescription().isBlank())
                object.put("description", fileType.getDescription());

            JsonObject acceptedTypes = Json.createObject();
            acceptedTypes.put(fileType.getMimeType(), asJsonArray(fileType.getFileExtensions()));

            object.put("accept", acceptedTypes);

            return object;
        }

        private JsonValue asJsonArray(String[] fileExtensions) {
            if(fileExtensions == null)
                return Json.createArray();

            JsonArray array = Json.createArray();
            for(int i = 0; i < fileExtensions.length; ++i)
                array.set(i, fileExtensions[i]);
            return array;
        }

        public FileInfo toFileInfo(JsonValue json) {
            JsonObject object = (JsonObject) json;

            return new FileInfo(
                    object.getString("name"),
                    DataSize.ofBytes((int) object.getNumber("size")),
                    object.getString("type"),
                    object.getString("content"));
        }
    }

}
