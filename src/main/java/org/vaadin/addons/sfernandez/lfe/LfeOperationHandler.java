package org.vaadin.addons.sfernandez.lfe;

import elemental.json.JsonValue;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.events.LfeCloseFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeCreateFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeOpenFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeSaveFileEvent;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;

import java.util.concurrent.CompletableFuture;

public class LfeOperationHandler {

    //---- Attributes ----
    private final LfeJsParameterHandler parameterHandler = new LfeJsParameterHandler();
    private final LfeErrorHandler errorHandler = new LfeErrorHandler();

    //---- Constructor ----
    LfeOperationHandler() {}

    //---- Methods ----
    public CompletableFuture<LfeCreateFileEvent> treatCreateFileJsRequest(final CompletableFuture<JsonValue> jsonResponse) {
        return jsonResponse.thenApply(json -> {
            if(errorHandler.thereIsAnError(json)) {
                LfeError error = errorHandler.creatingFileErrorOf(json);

                return new LfeCreateFileEvent(error);
            } else {
                FileInfo fileInfo = toFileInfo(json);

                return new LfeCreateFileEvent(fileInfo);
            }
        });
    }

    public CompletableFuture<LfeOpenFileEvent> treatOpenFileJsRequest(final CompletableFuture<JsonValue> jsonResponse) {
        return jsonResponse.thenApply(json -> {
            if(errorHandler.thereIsAnError(json)) {
                LfeError error = errorHandler.openingFileErrorOf(json);

                return new LfeOpenFileEvent(error);
            } else {
                FileInfo fileInfo = toFileInfo(json);

                return new LfeOpenFileEvent(fileInfo);
            }
        });
    }

    public CompletableFuture<LfeCloseFileEvent> treatCloseFileJsRequest(final CompletableFuture<JsonValue> jsonResponse) {
        return jsonResponse.thenApply(json -> {
            if(errorHandler.thereIsAnError(json)) {
                LfeError error = errorHandler.closingFileErrorOf(json);

                return new LfeCloseFileEvent(error);
            } else {
                FileInfo fileInfo = toFileInfo(json);

                return new LfeCloseFileEvent(fileInfo);
            }
        });
    }

    public CompletableFuture<LfeSaveFileEvent> treatSaveFileJsRequest(final CompletableFuture<JsonValue> jsonResponse, String content) {
        return jsonResponse.thenApply(json -> {
            if(errorHandler.thereIsAnError(json)) {
                LfeError error = errorHandler.savingFileErrorOf(json);

                return new LfeSaveFileEvent(content, error);
            } else {
                return new LfeSaveFileEvent(content);
            }
        });
    }

    private FileInfo toFileInfo(JsonValue json) {
        return parameterHandler.mapToFileInfo(json);
    }
}
