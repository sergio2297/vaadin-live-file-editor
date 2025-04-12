package org.vaadin.addons.sfernandez.lfe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class LiveFileEditor_StateTest {

    //---- Constants and Definitions ----

    //---- Attributes ----
    private LiveFileEditor editor;

    private LfeAutosave autosave;

    //---- Fixtures ----

    //---- Configuration ----
    @BeforeEach
    void setup() {
        autosave = Mockito.mock(LfeAutosave.class);
    }

    //---- Methods ----

    //---- Tests ----
//    @Test
//    void editorIsWorking_ifEditorIsWorkingTest() {
//        Mockito.when(editor.isWorking()).thenReturn(true);
//
//        LfeState state = editor.getState();
//
//        assertThat(state.editorIsWorking()).isTrue();
//    }
//
//    @Test
//    void editorIsNotWorking_ifEditorIsNotWorkingTest() {
//        Mockito.when(editor.isWorking()).thenReturn(false);
//
//        LfeState state = editor.getState();
//
//        assertThat(state.editorIsWorking()).isFalse();
//    }
//
//    @Test
//    void thereIsNoFileOpened_ifNoFileHasBeenOpenedByTheEditorTest() {
//        LfeState state = editor.getState();
//
//        assertThat(state.thereIsFileOpened()).isFalse();
//    }
//
//    @Test
//    void thereIsNoFileOpened_ifTheEditorHasNotOpenedAFileSuccessfullyTest() {
//
//    }
//
//    @Test
//    void thereIsFileOpened_ifTheEditorHasOpenedAFileSuccessfullyTest() {
//
//    }
//
//    @Test
//    void openedFileInfo_isEmpty_ifNoFileHasBeenOpenedByTheEditorTest() {
//
//    }
//
//    @Test
//    void openedFileInfo_changesToFileInfo_afterOpeningAFileTest() {
//
//    }
//
//    @Test
//    void openedFileInfo_changesToEmpty_afterClosingAFileTest() {
//
//    }
//
//    @Test
//    void lastSaveTime_isNull_ifEditorHasNoFileOpenedTest() {
//
//    }
//
//    @Test
//    void lastSaveTime_isNull_afterClosingCurrentFileTest() {
//
//    }
//
//    @Test
//    void lastSaveTime_isNull_ifEditorHasNeverSaveCurrentFileTest() {
//
//    }
//
//    @Test
//    void lastSaveTime_changeWhenEditorSaveFileSuccessfullyTest() {
//
//    }
//
//    @Test
//    void lastSaveResult_isNull_ifEditorHasNoFileOpenedTest() {
//
//    }
//
//    @Test
//    void lastSaveResult_isNull_afterClosingCurrentFileTest() {
//
//    }
//
//    @Test
//    void lastSaveResult_isNull_ifEditorHasNeverSaveCurrentFileTest() {
//
//    }
//
//    @Test
//    void lastSaveResult_changeWhenEditorSaveFileSuccessfullyTest() {
//
//    }
//
//    @Test
//    void lastSaveResult_changeWhenEditorSaveFileUnsuccessfullyTest() {
//
//    }
//
//    @Test
//    void autosaveIsRunning_ifAutosaveIsRunningTest() {
//        Mockito.when(autosave.isWorking()).thenReturn(true);
//
//        LfeState state = editor.getState();
//
//        assertThat(state.autosaveIsWorking()).isTrue();
//    }
//
//    @Test
//    void autosaveIsNotRunning_ifAutosaveIsNotRunningTest() {
//        Mockito.when(autosave.isWorking()).thenReturn(false);
//
//        LfeState state = editor.getState();
//
//        assertThat(state.autosaveIsWorking()).isFalse();
//    }

    @Test
    void lastAutosaveTime_isNull_ifAutosaveHasNotStartedYetTest() {

    }

    @Test
    void lastAutosaveTime_isNull_afterStoppingAutosaveTest() {

    }

    @Test
    void lastAutosaveTime_isNull_ifAutosaveHasNotOccurYetTest() {

    }

    @Test
    void lastAutosaveTime_changeWhenAutosaveIsExecutedSuccessfullyTest() {

    }

    @Test
    void lastAutosaveResult_isNull_ifAutosaveHasNotStartedYetTest() {

    }

    @Test
    void lastAutosaveResult_isNull_afterStoppingAutosaveTest() {

    }

    @Test
    void lastAutosaveResult_isNull_ifAutosaveHasNotOccurYetTest() {

    }

    @Test
    void lastAutosaveResult_changeWhenAutosaveIsExecutedSuccessfullyTest() {

    }

    @Test
    void lastAutosaveResult_changeWhenAutosaveIsExecutedUnsuccessfullyTest() {

    }

}