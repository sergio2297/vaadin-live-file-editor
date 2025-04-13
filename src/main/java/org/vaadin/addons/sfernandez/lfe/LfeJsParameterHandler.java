package org.vaadin.addons.sfernandez.lfe;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import es.sfernandez.library4j.types.DataSize;
import org.vaadin.addons.sfernandez.lfe.error.LiveFileEditorException;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;
import org.vaadin.addons.sfernandez.lfe.setup.FileType;

class LfeJsParameterHandler {

    //---- Attributes ----
    private final JsonParameterParser jsonParser = new JsonParameterParser();

    //---- Methods ----
    public JsonValue mapToCreateFileRequest(final String suggestedName, final FileType ... allowedFileTypes) {
        JsonObject json = Json.createObject();

        if(suggestedName != null && !suggestedName.isBlank())
            json.put("suggestedName", suggestedName);

        if(allowedFileTypes != null && allowedFileTypes.length > 0)
            json.put("types", jsonParser.asJson(allowedFileTypes));

        return json;
    }

    public JsonValue mapToOpenFileRequest(final FileType ... allowedFileTypes) {
        return jsonParser.asJson(allowedFileTypes);
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
                throw new LiveFileEditorException("Error. MIME type can't be null");

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
