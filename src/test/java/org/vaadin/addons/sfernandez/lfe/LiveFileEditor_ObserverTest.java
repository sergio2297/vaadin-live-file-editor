package org.vaadin.addons.sfernandez.lfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.events.LfeCloseFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeCreateFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeOpenFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeSaveFileEvent;
import org.vaadin.addons.sfernandez.lfe.setup.LfeAutosaveSetup;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class LiveFileEditor_ObserverTest {

    //---- Attributes ----
    private UiMock ui;
    private OperationHandlerMock operationHandler;

    private LiveFileEditor editor;
    private LfeObserver observer;

    //---- Configuration ----
    @BeforeEach
    void setup() {
        ui = new UiMock();
        operationHandler = new OperationHandlerMock();

        editor = new LiveFileEditor(ui, operationHandler.get());
        observer = editor.observer();

        editor.autosave().setEnabled(true);
        editor.autosave().setup(new LfeAutosaveSetup.Builder().dataToSaveSupplier(String::new).build());

        ui.attach();
    }

    //---- Methods ----

    //---- Tests ----
    @Test
    void observerNotifies_whenEditorStopWorking_becauseOfDetachTest() {
        UiMock ui = new UiMock();
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
        UiMock ui = new UiMock();
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
    void observerNotifies_whenEditorCreatesAFileSuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeCreateFileEvent eventThatWillBeFired = operationHandler.mockCreateFileToSuccess();

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addCreateFileListener(event -> isNotified.set(event == eventThatWillBeFired));

        editor.createFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorCreatesAFileUnsuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeCreateFileEvent eventThatWillBeFired = operationHandler.mockCreateFileToFail();

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addCreateFileListener(event -> isNotified.set(event == eventThatWillBeFired));

        editor.createFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorOpensAFileSuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeOpenFileEvent eventThatWillBeFired = operationHandler.mockOpenFileToSuccess();

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addOpenFileListener(event -> isNotified.set(event == eventThatWillBeFired));

        editor.openFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorOpensAFileUnsuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeOpenFileEvent eventThatWillBeFired = operationHandler.mockOpenFileToFail();

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addOpenFileListener(event -> isNotified.set(event == eventThatWillBeFired));

        editor.openFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorClosesAFileSuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeCloseFileEvent eventThatWillBeFired = operationHandler.mockCloseFileToSuccess();

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addCloseFileListener(event -> isNotified.set(event == eventThatWillBeFired));

        editor.closeFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorClosesAFileUnsuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeCloseFileEvent eventThatWillBeFired = operationHandler.mockCloseFileToFail();

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addCloseFileListener(event -> isNotified.set(event == eventThatWillBeFired));

        editor.closeFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorSavesFilesContentSuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeSaveFileEvent eventThatWillBeFired = operationHandler.mockSaveFileToSuccess("Saved data");

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addSaveFileListener(event -> isNotified.set(event == eventThatWillBeFired));

        editor.saveFile("Saved data").get(50, TimeUnit.MILLISECONDS);

        assertThat(isNotified).isTrue();
    }

    @Test
    void observerNotifies_whenEditorSavesFilesContentUnsuccessfullyTest() throws ExecutionException, InterruptedException, TimeoutException {
        LfeSaveFileEvent eventThatWillBeFired = operationHandler.mockSaveFileToFail("Unsaved data");

        AtomicBoolean isNotified = new AtomicBoolean(false);
        observer.addSaveFileListener(event -> isNotified.set(event == eventThatWillBeFired));

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

        ui.detach();

        assertThat(isWorking).isFalse();
    }

    @Test
    void observerNotifies_whenAutosaveStopsWorking_becauseOfClosingFileTest() throws ExecutionException, InterruptedException, TimeoutException {
        editor.autosave().start();
        assertThat(editor.autosave().isWorking()).isTrue();
        operationHandler.mockCloseFileToSuccess();

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
        operationHandler.mockOpenFileToSuccess();

        AtomicBoolean isWorking = new AtomicBoolean(false);
        observer.addAutosaveWorkingStateChangeListener(event -> isWorking.set(event.isWorking()));

        editor.openFile().get(50, TimeUnit.MILLISECONDS);

        assertThat(isWorking).isTrue();
    }

    // AutosaveListener is tested in LfeAutosaveTest

}