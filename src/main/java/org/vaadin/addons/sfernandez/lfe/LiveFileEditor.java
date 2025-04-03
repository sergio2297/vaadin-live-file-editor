package org.vaadin.addons.sfernandez.lfe;

import com.google.common.annotations.VisibleForTesting;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.JsModule;
import elemental.json.JsonValue;
import org.vaadin.addons.sfernandez.lfe.events.LfeCloseFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeOpenFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeSaveFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeWorkingStateChangeEvent;
import org.vaadin.addons.sfernandez.lfe.error.LiveFileEditorException;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;
import org.vaadin.addons.sfernandez.lfe.parameters.JsonParameterParser;
import org.vaadin.addons.sfernandez.lfe.setup.LiveFileEditorSetup;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@JsModule("./src/live-file-editor.js") // TODO: Puede que se haga abstracta para que sea ptra quien decida el esquema
public class LiveFileEditor {

    //---- Attributes ----
    private final Component attachment;

    private final JsonParameterParser jsonParser = new JsonParameterParser();

    private boolean isWorking = false;
    private LiveFileEditorSetup setup = new LiveFileEditorSetup();

    private final LfeOperationHandler operationHandler;
    private final LfeObserver observer = new LfeObserver();
    private final LfeAutosave autosave = new LfeAutosave(this);

    //---- Constructor ----
    @VisibleForTesting
    LiveFileEditor(Component attachment, LfeOperationHandler operationHandler) {
        this.attachment = attachment;
        this.operationHandler = operationHandler;

        init();
    }

    public LiveFileEditor(Component attachment) {
        this(attachment, new LfeOperationHandler());
    }

    private void init() {
        attachment.addAttachListener(attach -> start());
        attachment.addDetachListener(detach -> stop());
    }

    private void start() {
        isWorking = true;
        notifyWorkingStateChanged();
    }

    private void stop() {
//        if(isWorking()) // TODO: && hay fichero abierto
//            closeFile();

        if(autosave().isWorking())
            autosave().stop();

        isWorking = false;
        notifyWorkingStateChanged();
    }

    private void notifyWorkingStateChanged() {
        observer.notifyWorkingStateChangeEvent(new LfeWorkingStateChangeEvent(isWorking));
    }

    //---- Methods ----
    public Component getAttachment() {
        return attachment;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public boolean isNotWorking() {
        return !isWorking();
    }

    public void setup(final LiveFileEditorSetup setup) {
        this.setup = setup;
    }

    private void assertIsWorking() {
        if(isNotWorking())
            throw new LiveFileEditorException("Error. It's necessary to attach the attachment before using the LiveFileEditor.");
    }

    public CompletableFuture<Optional<FileInfo>> openFile() {
        assertIsWorking();

        CompletableFuture<LfeOpenFileEvent> opening = operationHandler.treatOpenFileJsRequest(sendOpenFileJsRequest());

        opening.thenAccept(event -> {
            observer.notifyOpenFileEvent(event);

            if(!event.failed() && autosave().isEnabled())
                autosave().start();
        });

        return opening.thenApply(LfeOpenFileEvent::fileInfo);
    }

    private CompletableFuture<JsonValue> sendOpenFileJsRequest() {
        return attachment.getElement()
                .executeJs("return await openFile($0);", allowedFileTypesAsJson())
                .toCompletableFuture();
    }

    public CompletableFuture<Optional<FileInfo>> closeFile() {
        assertIsWorking();

        if(autosave().isWorking())
            autosave().stop();

        CompletableFuture<LfeCloseFileEvent> closing = operationHandler.treatCloseFileJsRequest(sendCloseFileJsRequest());

        closing.thenAccept(observer::notifyCloseFileEvent);

        return closing.thenApply(LfeCloseFileEvent::fileInfo);
    }

    private CompletableFuture<JsonValue> sendCloseFileJsRequest() {
        return attachment.getElement()
                .executeJs("return await closeFile();")
                .toCompletableFuture();
    }

    private JsonValue allowedFileTypesAsJson() {
        return jsonParser.asJson(setup.getAllowedFileTypes());
    }

    // TODO: What if I try to save content and there isn't a file loaded => Save it as new could be a possibility
    // TODO: What if the file I opened before it's remove and now it's impossible to save it's content
    public CompletableFuture<Optional<String>> saveFile(final String content) {
        assertIsWorking();

        CompletableFuture<LfeSaveFileEvent> saving = operationHandler.treatSaveFileJsRequest(
                sendSaveFileJsRequest(content), content
        );

        saving.thenAccept(observer::notifySaveFileEvent)
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });

        return saving.thenApply(event ->
                event.failed()
                        ? Optional.empty()
                        : Optional.ofNullable(event.data())
        );
    }

    private CompletableFuture<JsonValue> sendSaveFileJsRequest(final String content) {
        return attachment.getElement()
                .executeJs("return saveFile($0)", content)
                .toCompletableFuture();
    }

    // TODO: Observer won't be the place to get the state
    public LfeState getState() {
        return new LfeState(
                isWorking(),
                false,
                autosave().isWorking()
        );
    }

    public LfeAutosave autosave() {
        return autosave;
    }

    public LfeObserver observer() {
        return observer;
    }

}
