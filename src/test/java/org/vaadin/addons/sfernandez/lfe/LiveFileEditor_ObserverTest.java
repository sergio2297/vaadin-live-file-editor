package org.vaadin.addons.sfernandez.lfe;

import com.vaadin.flow.component.*;
import es.sfernandez.library4j.types.DataSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.events.LfeCloseFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeOpenFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeSaveFileEvent;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;
import org.vaadin.addons.sfernandez.lfe.setup.LfeAutosaveSetup;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class LiveFileEditor_ObserverTest {

    //---- Constants and Definitions ----
    @Tag("div")
    private static final class MockedUi extends Component {

        //---- Attributes ----
        private final UI mockedUi = Mockito.mock(UI.class);

        //---- Constructor ----
        public MockedUi() {
//            Mockito.when(mockedUi.access(any())).then(invocation -> {
//                ((Runnable) invocation.getArgument(0)).run();
//                return null;
//            });
        }

        //---- Methods ----
        void attach() {
            super.fireEvent(new AttachEvent(mockedUi, true));
        }

        void detach() {
            super.fireEvent(new DetachEvent(mockedUi));
        }

        @Override
        public Optional<UI> getUI() {
            return Optional.of(mockedUi);
        }

    }

    //---- Attributes ----
    private MockedUi mockedUi;

    private LiveFileEditor editor;
    private LfeObserver observer;
    private LfeOperationHandler mockedOperationHandler;

    //---- Fixtures ----
    private LfeError dummyError() {
        return new LfeError(LfeError.Type.Other.UNKNOWN, "Unknown error");
    }

    private FileInfo dummyFileInfo() {
        return new FileInfo("file.txt", DataSize.ofBytes(96), "text/plain", "File content");
    }

    //---- Configuration ----
    @BeforeEach
    void setup() {
        mockedUi = new MockedUi();
        mockedOperationHandler = Mockito.mock(LfeOperationHandler.class);

        editor = new LiveFileEditor(mockedUi, mockedOperationHandler);
        observer = editor.observer();

        editor.autosave().setEnabled(true);
        editor.autosave().setup(new LfeAutosaveSetup.Builder().dataToSaveSupplier(String::new).build());

        mockedUi.attach();
    }

    //---- Methods ----

    //---- Tests ----
    @Test
    void observerNotifies_whenEditorStopWorking_becauseOfDetachTest() {
        MockedUi ui = new MockedUi();
        LiveFileEditor editor = new LiveFileEditor(ui);
        LfeObserver observer = editor.observer();

        ui.attach();
        assertThat(editor.isWorking()).isTrue();
        AtomicBoolean isWorking = new AtomicBoolean(true);
        observer.addWorkingStateChangeListener(event ->
                isWorking.set(event.isWorking())
        );

        ui.detach();

        assertThat(isWorking.get()).isFalse();
    }

    @Test
    void observerNotifies_whenEditorStartsWorking_becauseOfAttachTest() {
        MockedUi ui = new MockedUi();
        LiveFileEditor editor = new LiveFileEditor(ui);
        LfeObserver observer = editor.observer();

        assertThat(editor.isWorking()).isFalse();
        AtomicBoolean isWorking = new AtomicBoolean(false);
        observer.addWorkingStateChangeListener(event ->
                isWorking.set(event.isWorking())
        );

        ui.attach();

        assertThat(isWorking.get()).isTrue();
    }

    @Test
    void observerNotifies_whenEditorOpensAFileSuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeOpenFileEvent successfulEvent = new LfeOpenFileEvent(dummyFileInfo());
        Mockito.when(mockedOperationHandler.treatOpenFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(successfulEvent));

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addOpenFileListener(event -> isNotified.set(event == successfulEvent));

        editor.openFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorOpensAFileUnsuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeOpenFileEvent unsuccessfulEvent = new LfeOpenFileEvent(dummyError());
        Mockito.when(mockedOperationHandler.treatOpenFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(unsuccessfulEvent));

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addOpenFileListener(event -> isNotified.set(event == unsuccessfulEvent));

        editor.openFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorClosesAFileSuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeCloseFileEvent successfulEvent = new LfeCloseFileEvent(dummyFileInfo());
        Mockito.when(mockedOperationHandler.treatCloseFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(successfulEvent));

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addCloseFileListener(event -> isNotified.set(event == successfulEvent));

        editor.closeFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorClosesAFileUnsuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeCloseFileEvent unsuccessfulEvent = new LfeCloseFileEvent(dummyError());
        Mockito.when(mockedOperationHandler.treatCloseFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(unsuccessfulEvent));

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addCloseFileListener(event -> isNotified.set(event == unsuccessfulEvent));

        editor.closeFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorSavesFilesContentSuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeSaveFileEvent successfulEvent = new LfeSaveFileEvent("Saved data");
        Mockito.when(mockedOperationHandler.treatSaveFileJsRequest(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(successfulEvent));

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addSaveFileListener(event -> isNotified.set(event == successfulEvent));

        editor.saveFile("Saved data").get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorSavesFilesContentUnsuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeSaveFileEvent unsuccessfulEvent = new LfeSaveFileEvent("Unsaved data", dummyError());
        Mockito.when(mockedOperationHandler.treatSaveFileJsRequest(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(unsuccessfulEvent));

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addSaveFileListener(event -> isNotified.set(event == unsuccessfulEvent));

        editor.saveFile("Unsaved data").get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenAutosaveStopsWorking_becauseOfStoppingItExplicitlyTest() {
        editor.autosave().start();
        assertThat(editor.autosave().isWorking()).isTrue();

        AtomicBoolean isWorking = new AtomicBoolean(true);
        observer.addAutosaveWorkingStateChangeListener(event -> isWorking.set(event.isWorking()));

        editor.autosave().stop();

        assertThat(isWorking).isFalse();
    }

    @Test
    void observerNotifies_whenAutosaveStopsWorking_becauseOfStoppingEditorTest() {
        editor.autosave().start();
        assertThat(editor.autosave().isWorking()).isTrue();

        AtomicBoolean isWorking = new AtomicBoolean(true);
        observer.addAutosaveWorkingStateChangeListener(event -> isWorking.set(event.isWorking()));

        mockedUi.detach();

        assertThat(isWorking).isFalse();
    }

    @Test
    void observerNotifies_whenAutosaveStopsWorking_becauseOfClosingFileTest() throws ExecutionException, InterruptedException, TimeoutException {
        editor.autosave().start();
        assertThat(editor.autosave().isWorking()).isTrue();
        Mockito.when(mockedOperationHandler.treatCloseFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(new LfeCloseFileEvent(dummyFileInfo())));

        AtomicBoolean isWorking = new AtomicBoolean(true);
        observer.addAutosaveWorkingStateChangeListener(event -> isWorking.set(event.isWorking()));

        editor.closeFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isWorking).isFalse();
    }

    @Test
    void observerNotifies_whenAutosaveStartsWorking_becauseOfStartingItExplicitlyTest() {
        assertThat(editor.autosave().isWorking()).isFalse();

        AtomicBoolean isWorking = new AtomicBoolean(false);
        observer.addAutosaveWorkingStateChangeListener(event -> isWorking.set(event.isWorking()));

        editor.autosave().start();

        assertThat(isWorking).isTrue();
    }

    @Test
    void observerNotifies_whenAutosaveStartsWorking_becauseOpeningFileTest() throws ExecutionException, InterruptedException, TimeoutException {
        assertThat(editor.autosave().isWorking()).isFalse();
        Mockito.when(mockedOperationHandler.treatOpenFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(new LfeOpenFileEvent(dummyFileInfo())));

        AtomicBoolean isWorking = new AtomicBoolean(false);
        observer.addAutosaveWorkingStateChangeListener(event -> isWorking.set(event.isWorking()));

        editor.openFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isWorking).isTrue();
    }

    // AutosaveListener is tested in LfeAutosaveTest

}