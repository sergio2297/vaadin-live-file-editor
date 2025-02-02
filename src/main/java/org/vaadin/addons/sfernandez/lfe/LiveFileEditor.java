package org.vaadin.addons.sfernandez.lfe;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import elemental.json.JsonValue;
import org.checkerframework.checker.units.qual.C;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;
import org.vaadin.addons.sfernandez.lfe.parameters.JsonParameterParser;
import org.vaadin.addons.sfernandez.lfe.setup.LiveFileEditorSetup;

import java.util.concurrent.CompletableFuture;

@Tag("div")
@JsModule("./src/live-file-editor.js") // TODO: Puede que se haga abstracta para que sea ptra quien decida el esquema
public class LiveFileEditor extends Component {

    //---- Attributes ----
    private final JsonParameterParser jsonParser = new JsonParameterParser();
    private LiveFileEditorSetup setup = new LiveFileEditorSetup();

    //---- Constructor ----
    public LiveFileEditor() {
        getElement().appendChild(new Div("Hello").getElement());
    }

    //---- Methods ----
    public void setup(final LiveFileEditorSetup setup) {
        this.setup = setup;
    }

    public CompletableFuture<FileInfo> openFile() {
        CompletableFuture<FileInfo> openedFile = new CompletableFuture<>();

        getElement().executeJs("return await openFile($0);", allowedFileTypesAsJson())
                .then(json -> openedFile.complete(toFileInfo(json))); // TODO: error catching

        return openedFile;
    }

    private JsonValue allowedFileTypesAsJson() {
        return jsonParser.asJson(setup.getAllowedFileTypes());
    }

    private FileInfo toFileInfo(JsonValue json) {
        return jsonParser.toFileInfo(json);
    }

    public void saveFile(final String content) {
        getElement().executeJs("mySaveFile($0)", content);
    }

}
