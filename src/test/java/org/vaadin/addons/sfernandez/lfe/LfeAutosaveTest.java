package org.vaadin.addons.sfernandez.lfe;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vaadin.addons.sfernandez.lfe.error.LfeException;
import org.vaadin.addons.sfernandez.lfe.events.LfeSaveFileEvent;
import org.vaadin.addons.sfernandez.lfe.setup.LfeAutosaveSetup;

import java.io.PrintStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LfeAutosaveTest {

    //---- Attributes ----
    private LiveFileEditor mockedEditor;
    private LfeObserver mockedObserver;
    private UI mockedUi;

    private LfeAutosave autosave;

    //---- Configuration ----
    @BeforeEach
    void setup() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .build();

        mockedUi = Mockito.mock(UI.class);
        Mockito.when(mockedUi.getUI()).thenReturn(Optional.of(mockedUi));
        Mockito.when(mockedUi.access(Mockito.any())).then(invocation -> {
            invocation.getArgument(0, Command.class).execute();
            return CompletableFuture.completedFuture(null);
        });

        mockedObserver = Mockito.mock(LfeObserver.class);

        mockedEditor = Mockito.mock(LiveFileEditor.class);
        Mockito.when(mockedEditor.isWorking()).thenReturn(true);
        Mockito.when(mockedEditor.isNotWorking()).thenReturn(false);
        Mockito.when(mockedEditor.getAttachment()).thenReturn(mockedUi);
        Mockito.when(mockedEditor.observer()).thenReturn(mockedObserver);

        autosave = new LfeAutosave(mockedEditor);
        autosave.setEnabled(true);
        autosave.setup(setup);
    }

    //---- Methods ----
    private PrintStream mockSystemStandardOut() {
        PrintStream standardOut = Mockito.mock(PrintStream.class);
        System.setOut(standardOut);
        return standardOut;
    }

    //---- Tests ----
    @Test
    void autosave_isDisabled_byDefaultTest() {
        LfeAutosave autosave = new LfeAutosave(mockedEditor);

        assertThat(autosave.isEnabled()).isFalse();
    }

    @Test
    void setEnable_changeAutosaveStatusTest() {
        LfeAutosave autosave = new LfeAutosave(mockedEditor);

        autosave.setEnabled(true);

        assertThat(autosave.isEnabled()).isTrue();
    }

    @Test
    void setEnable_doesNotStartAutosaveTest() {
        LfeAutosave autosave = new LfeAutosave(mockedEditor);

        autosave.setEnabled(true);

        assertThat(autosave.isWorking()).isFalse();
    }

    @Test
    void setEnableToFalse_stopsAutosaveIfWorkingTest() {
        autosave.start();

        autosave.setEnabled(false);

        assertThat(autosave.isWorking()).isFalse();
    }

    @Test
    void autosave_isNotRunning_byDefaultTest() {
        LfeAutosave autosave = new LfeAutosave(mockedEditor);

        assertThat(autosave.isWorking()).isFalse();
    }

    @Test
    void start_withoutEnable_doesNotStartAutosaveTest() {
        autosave.setEnabled(false);
        autosave.start();

        assertThat(autosave.isWorking()).isFalse();
    }

    @Test
    void start_withoutSetup_throwsAnExceptionTest() {
        autosave.setup(null);

        assertThrows(LfeException.class, autosave::start);
    }

    @Test
    void autosave_doesNotHaveAnySetupByDefaultTest() {
        LfeAutosave autosave = new LfeAutosave(mockedEditor);
        autosave.setEnabled(true);

        assertThrows(LfeException.class, autosave::start);
    }

    @Test
    void start_withoutWorkingEditor_throwsAnExceptionTest() {
        Mockito.when(mockedEditor.isNotWorking()).thenReturn(true);
        Mockito.when(mockedEditor.isWorking()).thenReturn(false);

        assertThrows(LfeException.class, autosave::start);
    }

    @Test
    void start_whenEditorAttachment_hasNotGotAnUI_throwsExceptionTest() {
        Mockito.when(mockedUi.getUI()).thenReturn(Optional.empty());

        assertThrows(LfeException.class, autosave::start);
    }

    @Test
    void start_withoutHavingPreviouslyOpenedAFile_throwsAnExceptionTest() {
        // TODO
    }

    @Test
    void afterStart_itIsRunningTest() {
        autosave.start();

        assertThat(autosave.isWorking()).isTrue();
    }

    @Test
    void afterStart_withoutAllowingToAlterUiPollInterval_andUIPollIntervalIsDisabled_thenAWarningIsShownTest() {
        PrintStream standardOut = mockSystemStandardOut();
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .allowToAlterUiPollInterval(false)
                .build();
        autosave.setup(setup);
        Mockito.when(mockedUi.getPollInterval()).thenReturn(-1);

        autosave.start();

        Mockito.verify(standardOut).println("Warning! Ui poll interval is disabled or is larger than the autosave process frequency. This could end causing outdated saves.");
    }

    @Test
    void afterStart_withoutAllowingToAlterUiPollInterval_andUIPollIntervalIsLowerThanAutosaveFrequency_thenAWarningIsShown_onlyOnceTest() {
        PrintStream standardOut = mockSystemStandardOut();
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .allowToAlterUiPollInterval(false)
                .build();
        autosave.setup(setup);
        Mockito.when(mockedUi.getPollInterval()).thenReturn((int) (setup.frequency().toMillis() - 1));

        autosave.start();

        Mockito.verify(standardOut).println("Warning! Ui poll interval is disabled or is larger than the autosave process frequency. This could end causing outdated saves.");
    }

    @Test
    void afterStart_withoutAllowingToAlterUiPollInterval_andUIPollIntervalIsHigherThanAutosaveFrequency_thenNoWarningIsShownTest() {
        PrintStream standardOut = mockSystemStandardOut();
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .allowToAlterUiPollInterval(false)
                .build();
        autosave.setup(setup);
        Mockito.when(mockedUi.getPollInterval()).thenReturn((int) (setup.frequency().toMillis() + 1));

        autosave.start();

        Mockito.verifyNoInteractions(standardOut);
    }

    @Test
    void afterStart_allowingToAlterUiPollInterval_whenUIPollIntervalIsDisabled_thenUIPollIntervalIsAutosaveFrequencyTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .allowToAlterUiPollInterval(true)
                .build();
        autosave.setup(setup);
        Mockito.when(mockedUi.getPollInterval()).thenReturn(-1);

        autosave.start();

        Mockito.verify(mockedUi).setPollInterval((int) setup.frequency().toMillis());
    }

    @Test
    void afterStart_allowingToAlterUiPollInterval_whenUIPollIntervalIsLowerThanAutosaveFrequency_thenUIPollIntervalIsAutosaveFrequencyTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .allowToAlterUiPollInterval(true)
                .build();
        autosave.setup(setup);
        Mockito.when(mockedUi.getPollInterval()).thenReturn((int) (setup.frequency().toMillis() - 1));

        autosave.start();

        Mockito.verify(mockedUi).setPollInterval((int) setup.frequency().toMillis());
    }

    @Test
    void afterStart_allowingToAlterUiPollInterval_whenUIPollIntervalIsHigherThanAutosaveFrequency_thenUIPollIntervalIsNotAlteredTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .allowToAlterUiPollInterval(true)
                .build();
        autosave.setup(setup);
        Mockito.when(mockedUi.getPollInterval()).thenReturn((int) (setup.frequency().toMillis() + 1));

        autosave.start();

        Mockito.verify(mockedUi, Mockito.never()).setPollInterval(Mockito.anyInt());
    }

    @Test
    void afterStop_itIsNotRunningTest() {
        autosave.start();
        autosave.stop();

        assertThat(autosave.isWorking()).isFalse();
    }

    @Test
    void afterStop_allowingAlterUIPollInterval_thenUiPollIntervalIsRestoredTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .allowToAlterUiPollInterval(true)
                .build();
        autosave.setup(setup);
        Mockito.when(mockedUi.getPollInterval()).thenReturn(-1);
        autosave.start();

        autosave.stop();

        Mockito.verify(mockedUi).setPollInterval(-1);
    }

    @Test
    void afterStop_withoutAllowingAlterUIPollInterval_thenUiPollIntervalIsNotAlteredTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .allowToAlterUiPollInterval(false)
                .build();
        autosave.setup(setup);
        Mockito.when(mockedUi.getPollInterval()).thenReturn(-1);
        autosave.start();

        autosave.stop();

        Mockito.verify(mockedUi, Mockito.never()).setPollInterval(Mockito.anyInt());
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
    void whenRunning_dataIsSavedAutomatically_whenDataToSaveHasChanged_Test() throws InterruptedException, ExecutionException {
        AtomicInteger autosaveExecutions = new AtomicInteger(0);
        CompletableFuture<Boolean> contentSavedFiveTimes = new CompletableFuture<>();

        AtomicReference<String> dataToSave = new AtomicReference<>("");

        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(dataToSave::get)
                .build();
        autosave.setup(setup);

        Mockito.when(mockedEditor.saveFile(Mockito.any()))
                .then(invocation -> {
                    if(autosaveExecutions.incrementAndGet() >= 5)
                        contentSavedFiveTimes.complete(true);

                    dataToSave.set(" ".repeat(autosaveExecutions.get()));

                    return CompletableFuture.completedFuture(true);
                });

        autosave.start();

        contentSavedFiveTimes.get();
        Mockito.verify(mockedEditor, Mockito.atLeast(5)).saveFile(Mockito.anyString());
    }

    @Test
    void whenRunning_autosaveIsNotExecuted_whenDataHasNotChanged_Test() throws ExecutionException, InterruptedException {
        AtomicInteger autosaveExecutions = new AtomicInteger(0);
        CompletableFuture<Boolean> contentSavedFiveTimes = new CompletableFuture<>();

        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .build();
        autosave.setup(setup);

        Mockito.when(mockedEditor.saveFile(Mockito.any()))
                .then(invocation -> {
                    if(autosaveExecutions.incrementAndGet() >= 5)
                        contentSavedFiveTimes.complete(true);

                    return CompletableFuture.completedFuture(true);
                });

        autosave.start();

        try {
            contentSavedFiveTimes.get(setup.frequency().toMillis() * 6, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Mockito.verify(mockedEditor, Mockito.atMost(1)).saveFile("");
        }
    }

    @Test
    void ifAutosaveFails_thenAnotherAutosaveTryWillBeExecutedLaterTest() throws ExecutionException, InterruptedException {
        AtomicInteger autosaveExecutions = new AtomicInteger(0);
        CompletableFuture<Boolean> autosaveTries = new CompletableFuture<>();

        LfeAutosaveSetup setup = new LfeAutosaveSetup.Builder()
                .frequency(Duration.ofMillis(10L))
                .dataToSaveSupplier(String::new)
                .build();
        autosave.setup(setup);

        Mockito.when(mockedEditor.saveFile(Mockito.any()))
                .then(invocation -> {
                    if(autosaveExecutions.incrementAndGet() >= 5)
                        autosaveTries.complete(true);

                    return CompletableFuture.completedFuture(false);
                });

        autosave.start();

        autosaveTries.get();
        Mockito.verify(mockedEditor, Mockito.atLeast(5)).saveFile("");
    }

    @Test
    void start_whenAutosaveIsAlreadyRunning_stopBeforeRestartingTest() throws InterruptedException {
        Mockito.when(mockedEditor.saveFile(Mockito.any()))
                .then(invocation -> new CompletableFuture<>());
        autosave.start();

        int numOfTries = 0;
        CompletableFuture<?> saveInProgress;
        while((saveInProgress = autosave.getSaveInProgress()) == null && numOfTries++ < 5)
            Thread.sleep(5L);

        if(saveInProgress == null)
            fail("Error. Autosave process haven't started yet.");
        else {
            autosave.start();
            assertThat(saveInProgress.isCancelled()).isTrue();
        }
    }

    @Test
    void addedAutosaveListener_isNotifiedWithDataToSave_whenAutosaveOccurTest() throws ExecutionException, InterruptedException {
        String dataToSave = "Data to save";
        autosave.setup(new LfeAutosaveSetup.Builder()
                .dataToSaveSupplier(() -> dataToSave)
                .build()
        );
        Mockito.when(mockedEditor.saveFile(dataToSave))
                .then(invocation -> CompletableFuture.completedFuture(Optional.of(dataToSave)));
        CompletableFuture<LfeSaveFileEvent> autosaveOperation = new CompletableFuture<>();
        Mockito.doAnswer(invocation -> autosaveOperation.complete(invocation.getArgument(0)))
                .when(mockedObserver).notifyAutosaveFileEvent(Mockito.any());

        autosave.start();

        try {
            assertThat(autosaveOperation.get(50L, TimeUnit.MILLISECONDS).data()).isEqualTo(dataToSave);
        } catch (TimeoutException e) {
            fail("Error. Autosave process haven't started yet.");
        }
    }

}