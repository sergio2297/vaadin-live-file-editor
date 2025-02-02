package org.vaadin.addons.sfernandez.lfe.parameters;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import es.sfernandez.library4j.types.DataSize;
import org.vaadin.addons.sfernandez.lfe.LiveFileEditorException;
import org.vaadin.addons.sfernandez.lfe.setup.FileType;

public class JsonParameterParser {

    //---- Methods ----
    public JsonValue asJson(FileType ... fileTypes) {
        JsonObject json = Json.createObject();
        json.put("types", asJsonArray(fileTypes));
        return json;
    }

    private JsonValue asJsonArray(FileType[] fileTypes) {
        if(fileTypes == null)
            return Json.createArray();

        JsonArray array = Json.createArray();
        for(int i = 0; i < fileTypes.length; ++i)
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
