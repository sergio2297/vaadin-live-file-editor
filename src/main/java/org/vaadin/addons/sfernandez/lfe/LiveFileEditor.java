package org.vaadin.addons.sfernandez.lfe;

import com.google.common.annotations.VisibleForTesting;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.dependency.JsModule;
import elemental.json.JsonValue;
import org.vaadin.addons.sfernandez.lfe.error.LfeException;
import org.vaadin.addons.sfernandez.lfe.events.*;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;
import org.vaadin.addons.sfernandez.lfe.parameters.OptionsCreateFile;
import org.vaadin.addons.sfernandez.lfe.parameters.OptionsHandlingFilePicker;
import org.vaadin.addons.sfernandez.lfe.parameters.OptionsOpenFile;
import org.vaadin.addons.sfernandez.lfe.setup.LfeSetup;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@JsModule("./src/live-file-editor.js")
public class LiveFileEditor {

    //---- Attributes ----
    private final String uuid = UUID.randomUUID().toString().substring(0, 32);
    private final Component attachment;

    private boolean isWorking = false;
    private LfeSetup setup = new LfeSetup();

    private final LfeOperationHandler operationHandler;
    private final LfeJsParameterHandler jsParameterHandler = new LfeJsParameterHandler();
    private final LfeObserver observer = new LfeObserver();
    private final LfeAutosave autosave = new LfeAutosave(this);
    private LfeState state = new LfeState();

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
        updateState();
    }

    private void stop() {
        if(isWorking() && state().thereIsFileOpened())
            closeFile();

        if(autosave().isWorking())
            autosave().stop();

        isWorking = false;
        notifyWorkingStateChanged();
        updateState();
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

    public void setup(final LfeSetup setup) {
        this.setup = setup;
    }

    private void assertIsWorking() {
        if(isNotWorking())
            throw new LfeException("Error. It's necessary to attach the attachment before using the LiveFileEditor.");
    }

    private void assertAttachmentIsReadyToSendJsRequest() {
        if(!attachment.isVisible())
            // If the attachment isn't visible, the JS request will be received at the Client side when it becomes visible again
            throw new LfeException("Error. The attachment must be visible in order to execute this operation.\n" +
                    "You can always create the lfe linking it to another attachment (e.g.: UI) that will be always visible," +
                    "or at least at the moment when this operation is executed.");

        if(attachment instanceof HasEnabled enabledAttachment && !enabledAttachment.isEnabled())
            // If the attachment isn't enable, the JS request will be sent but the server side will never receive the response
            // cause of: "Ignoring update for disabled return channel"
            throw new LfeException("Error. The attachment must be enabled in order to execute this operation.\n" +
                    "You can always create the lfe linking it to another attachment (e.g.: UI) that will be always enabled," +
                    "or at least at the moment when this operation is executed.");
    }

    private void prepareOptions(final OptionsHandlingFilePicker options) {
        if(setup.isRememberLastDirectory())
            options.setId(uuid);
    }

    public CompletableFuture<Optional<FileInfo>> createFile() {
        return createFile((String) null);
    }

    public CompletableFuture<Optional<FileInfo>> createFile(final String suggestedName) {
        OptionsCreateFile options = new OptionsCreateFile();
        options.setExcludeAcceptAllOption(!setup.isAllFileTypesAllowed());
        options.setAllowedFileTypes(setup.getAllowedFileTypes());
        options.setSuggestedName(suggestedName);

        return createFile(options);
    }

    public CompletableFuture<Optional<FileInfo>> createFile(final OptionsCreateFile options) {
        assertIsWorking();
        prepareOptions(options);

        CompletableFuture<LfeCreateFileEvent> creating = operationHandler.treatCreateFileJsRequest(sendCreateFileJsRequest(options));

        creating.thenAccept(observer::notifyCreateFileEvent);
        creating.thenAccept(this::updateState);
        creating.thenAccept(event -> {
            if(!event.failed() && autosave().isEnabled())
                autosave().start();
        });

        return creating.thenApply(LfeCreateFileEvent::fileInfo);
    }

    private CompletableFuture<JsonValue> sendCreateFileJsRequest(final OptionsCreateFile options) {
        assertAttachmentIsReadyToSendJsRequest();

        return attachment.getElement()
                .executeJs("return await createFile($0);",
                        jsParameterHandler.mapToJson(options))
                .toCompletableFuture();
    }

    public CompletableFuture<Optional<FileInfo>> openFile() {
        OptionsOpenFile options = new OptionsOpenFile();
        options.setExcludeAcceptAllOption(!setup.isAllFileTypesAllowed());
        options.setAllowedFileTypes(setup.getAllowedFileTypes());

        return openFile(options);
    }

    public CompletableFuture<Optional<FileInfo>> openFile(final OptionsOpenFile options) {
        assertIsWorking();
        prepareOptions(options);

        CompletableFuture<LfeOpenFileEvent> opening = operationHandler.treatOpenFileJsRequest(sendOpenFileJsRequest(options));

        opening.thenAccept(observer::notifyOpenFileEvent);
        opening.thenAccept(this::updateState);
        opening.thenAccept(event -> {
            if(!event.failed() && autosave().isEnabled())
                autosave().start();
        });

        return opening.thenApply(LfeOpenFileEvent::fileInfo);
    }

    private CompletableFuture<JsonValue> sendOpenFileJsRequest(OptionsOpenFile options) {
        assertAttachmentIsReadyToSendJsRequest();

        return attachment.getElement()
                .executeJs("return await openFile($0);",
                        jsParameterHandler.mapToJson(options))
                .toCompletableFuture();
    }

    public CompletableFuture<Optional<FileInfo>> closeFile() {
        assertIsWorking();

        if(autosave().isWorking())
            autosave().stop();

        CompletableFuture<LfeCloseFileEvent> closing = operationHandler.treatCloseFileJsRequest(sendCloseFileJsRequest());

        closing.thenAccept(observer::notifyCloseFileEvent);
        closing.thenAccept(this::updateState);

        return closing.thenApply(LfeCloseFileEvent::fileInfo);
    }

    private CompletableFuture<JsonValue> sendCloseFileJsRequest() {
        assertAttachmentIsReadyToSendJsRequest();

        return attachment.getElement()
                .executeJs("return await closeFile();")
                .toCompletableFuture();
    }

    public CompletableFuture<Optional<String>> saveFile(final String content) {
        assertIsWorking();

        CompletableFuture<LfeSaveFileEvent> saving = operationHandler.treatSaveFileJsRequest(
                sendSaveFileJsRequest(content), content
        );

        saving.thenAccept(observer::notifySaveFileEvent);
        saving.thenAccept(this::updateState);

        return saving.thenApply(event ->
                event.failed()
                        ? Optional.empty()
                        : Optional.ofNullable(event.data())
        );
    }

    private CompletableFuture<JsonValue> sendSaveFileJsRequest(final String content) {
        assertAttachmentIsReadyToSendJsRequest();

        return attachment.getElement()
                .executeJs("return saveFile($0)", content)
                .toCompletableFuture();
    }

    void updateState() {
        updateState(null);
    }

    private void updateState(LfeOperationEvent event) {
        if(event != null && event.failed())
            return;

        LfeState oldState = state;

        state = state.withEditorIsWorking(isWorking())
                .withAutosaveIsWorking(autosave().isWorking());

        if(event instanceof LfeOpenFileEvent opening && opening.fileInfo().isPresent())
            state = state.withOpenedFile(opening.fileInfo().get());
        else if(event instanceof LfeCloseFileEvent)
            state = state.withOpenedFile(null);
        else if(event instanceof LfeSaveFileEvent saving) {
            state = state.withLastSaveTime(LocalDateTime.now())
                    .withLastSaveData(saving.data());
        }

        if(!oldState.equals(state))
            observer.notifyStateChangeEvent(new LfeStateChangeEvent(state, oldState));
    }

    public LfeState state() {
        return state;
    }

    public LfeAutosave autosave() {
        return autosave;
    }

    public LfeObserver observer() {
        return observer;
    }

}
