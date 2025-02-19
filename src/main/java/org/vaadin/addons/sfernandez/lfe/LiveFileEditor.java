package org.vaadin.addons.sfernandez.lfe;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.JsModule;
import elemental.json.JsonValue;
import org.vaadin.addons.sfernandez.lfe.components.autosave.LfeAutosave;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;
import org.vaadin.addons.sfernandez.lfe.parameters.JsonParameterParser;
import org.vaadin.addons.sfernandez.lfe.setup.LiveFileEditorSetup;

import java.util.concurrent.CompletableFuture;

@JsModule("./src/live-file-editor.js") // TODO: Puede que se haga abstracta para que sea ptra quien decida el esquema
public class LiveFileEditor {

    //---- Attributes ----
    private final Component attachment;

    private final JsonParameterParser jsonParser = new JsonParameterParser();
    private LiveFileEditorSetup setup = new LiveFileEditorSetup();

    private boolean isWorking = false;
    private final LfeAutosave autosave = new LfeAutosave(this);

    //---- Constructor ----
    public LiveFileEditor(Component attachment) {
        this.attachment = attachment;
        init();
    }

    private void init() {
        attachment.addAttachListener(attach -> start());
        attachment.addDetachListener(detach -> stop());
    }

    private void start() {
        isWorking = true;
    }

    private void stop() {
        isWorking = false;

        if(isWorking()) // TODO: && hay fichero abierto
            closeFile();
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

    public CompletableFuture<FileInfo> openFile() {
        assertIsWorking();

        CompletableFuture<FileInfo> openedFile = new CompletableFuture<>();

        attachment.getElement().executeJs("return await openFile($0);", allowedFileTypesAsJson())
                .then(json -> openedFile.complete(toFileInfo(json))); // TODO: error catching

        openedFile.thenRun(() -> {
            if(autosave().isEnabled())
                autosave().start();
        });

        return openedFile;
    }

    public CompletableFuture<Boolean> closeFile() {
        assertIsWorking();

        CompletableFuture<Boolean> fileClosed = new CompletableFuture<>();

        attachment.getElement().executeJs("return await closeFile();")
                .then(json -> fileClosed.complete(true)); // TODO: error catching

        fileClosed.thenRun(() -> {
            if(autosave().isRunning())
                autosave().stop();
        });

        return fileClosed;
    }

    private JsonValue allowedFileTypesAsJson() {
        return jsonParser.asJson(setup.getAllowedFileTypes());
    }

    private FileInfo toFileInfo(JsonValue json) {
        return jsonParser.toFileInfo(json);
    }

    // TODO: What if I try to save content and there isn't a file loaded => Save it as new could be a possibility
    // TODO: What if the file I opened before it's remove and now it's impossible to save it's content
    public CompletableFuture<Boolean> saveFile(final String content) {
        assertIsWorking();

        CompletableFuture<Boolean> fileSaved = new CompletableFuture<>();

        attachment.getElement().executeJs("return saveFile($0)", content)
                .then(json -> fileSaved.complete(true));

        return fileSaved;
    }

    public LfeAutosave autosave() {
        return autosave;
    }

}
