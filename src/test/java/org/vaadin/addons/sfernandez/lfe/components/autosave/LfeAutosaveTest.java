package org.vaadin.addons.sfernandez.lfe.components.autosave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vaadin.addons.sfernandez.lfe.LiveFileEditor;
import org.vaadin.addons.sfernandez.lfe.LiveFileEditorException;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LfeAutosaveTest {

    //---- Attributes ----
    private LiveFileEditor mockedEditor;

    private LfeAutosave autosave;

    //---- Configuration ----
    @BeforeEach
    void setup() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup();
        setup.setFrequency(Duration.ofMillis(10L));
        setup.setDataToSave(String::new);

        mockedEditor = Mockito.mock(LiveFileEditor.class);
        Mockito.when(mockedEditor.isNotWorking()).thenReturn(false);

        autosave = new LfeAutosave(mockedEditor);
        autosave.setup(setup);
    }

    //---- Tests ----
    @Test
    void autoSave_isDisabled_byDefaultTest() {
        LfeAutosave autosave = new LfeAutosave(mockedEditor);

        assertThat(autosave.isEnabled()).isFalse();
    }

    @Test
    void autosave_isNotRunning_byDefaultTest() {
        assertThat(autosave.isRunning()).isFalse();
    }

    @Test
    void afterStart_itIsRunningTest() {
        autosave.start();

        assertThat(autosave.isRunning()).isTrue();
    }

    @Test
    void afterStop_itIsNotRunningTest() {
        autosave.start();
        autosave.stop();

        assertThat(autosave.isRunning()).isFalse();
    }

    @Test
    void setup_withoutContentToSaveSupplier_throwsExceptionTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup();
        setup.setDataToSave(null);

        assertThrows(LiveFileEditorException.class, () -> autosave.setup(setup));
    }

    @Test
    void start_withoutHavingSetupIt_throwsExceptionTest() {
        LfeAutosave autosave = new LfeAutosave(mockedEditor);

        assertThrows(LiveFileEditorException.class, autosave::start);
    }

    @Test
    void afterLosingReference_itsExecutorService_isShutDownedTest() throws InterruptedException {
        ExecutorService executorService = autosave.getExecutorService();
        autosave = null;

        for(int i = 0; i < 10; ++i) {
            System.gc();
            Thread.sleep(10);

            if(executorService.isShutdown())
                break;
        }

        assertThat(executorService.isShutdown()).isTrue();
    }

    @Test
    void afterStart_contentIsSavedAutomatically_Test() throws InterruptedException, ExecutionException {
        AtomicInteger autosaveExecutions = new AtomicInteger(0);
        CompletableFuture<Boolean> contentSavedFiveTimes = new CompletableFuture<>();

        Mockito.when(mockedEditor.saveFile(Mockito.any()))
                .then(content -> {
                    if(autosaveExecutions.incrementAndGet() >= 5)
                        contentSavedFiveTimes.complete(true);

                    return CompletableFuture.completedFuture(true);
                });

        autosave.start();

        contentSavedFiveTimes.get();
        Mockito.verify(mockedEditor, Mockito.atLeast(5)).saveFile("");
    }

    @Test
    void start_withoutWorkingEditor_throwsExceptionTest() {
        Mockito.when(mockedEditor.isNotWorking()).thenReturn(true);

        assertThrows(LiveFileEditorException.class, autosave::start);
    }

}