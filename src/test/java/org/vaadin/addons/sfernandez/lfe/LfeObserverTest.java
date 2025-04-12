package org.vaadin.addons.sfernandez.lfe;

import com.vaadin.flow.shared.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vaadin.addons.sfernandez.lfe.events.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class LfeObserverTest {

    //---- Attributes ----
    private LfeObserver observer;

    //---- Configuration ----
    @BeforeEach
    void setup() {
        this.observer = new LfeObserver();
    }

    //---- Tests ----
    @Test
    void addedStateChangeListeners_areNotified_withStateChangeEventsTest() {
        LfeStateChangeEvent event = Mockito.mock(LfeStateChangeEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        observer.addStateChangeListener(
                e -> notified.set(e == event)
        );
        observer.notifyStateChangeEvent(event);

        assertThat(notified.get()).isTrue();
    }

    @Test
    void removedStateChangeListeners_areNotNotifiedAnyMore_withStateChangeEventsTest() {
        LfeStateChangeEvent event = Mockito.mock(LfeStateChangeEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        Registration registration = observer.addStateChangeListener(
                e -> notified.set(e == event)
        );
        registration.remove();
        observer.notifyStateChangeEvent(event);

        assertThat(notified.get()).isFalse();
    }

    @Test
    void addedWorkingStateChangeListeners_areNotified_withWorkingStateChangeEventsTest() {
        LfeWorkingStateChangeEvent event = Mockito.mock(LfeWorkingStateChangeEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        observer.addWorkingStateChangeListener(
                e -> notified.set(e == event)
        );
        observer.notifyWorkingStateChangeEvent(event);

        assertThat(notified.get()).isTrue();
    }

    @Test
    void removedWorkingStateChangeListeners_areNotNotifiedAnyMore_withWorkingStateChangeEventsTest() {
        LfeWorkingStateChangeEvent event = Mockito.mock(LfeWorkingStateChangeEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        Registration registration = observer.addWorkingStateChangeListener(
                e -> notified.set(e == event)
        );
        registration.remove();
        observer.notifyWorkingStateChangeEvent(event);

        assertThat(notified.get()).isFalse();
    }

    @Test
    void addedAutosaveWorkingStateChangeListeners_areNotified_withAutosaveWorkingStateChangeEventsTest() {
        LfeAutosaveWorkingStateChangeEvent event = Mockito.mock(LfeAutosaveWorkingStateChangeEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        observer.addAutosaveWorkingStateChangeListener(
                e -> notified.set(e == event)
        );
        observer.notifyAutosaveWorkingStateChangeEvent(event);

        assertThat(notified.get()).isTrue();
    }

    @Test
    void removedAutosaveWorkingStateChangeListeners_areNotNotifiedAnyMore_withAutosaveWorkingStateChangeEventsTest() {
        LfeAutosaveWorkingStateChangeEvent event = Mockito.mock(LfeAutosaveWorkingStateChangeEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        Registration registration = observer.addAutosaveWorkingStateChangeListener(
                e -> notified.set(e == event)
        );
        registration.remove();
        observer.notifyAutosaveWorkingStateChangeEvent(event);

        assertThat(notified.get()).isFalse();
    }

    @Test
    void addedCreateListeners_areNotified_withCreateEventsTest() {
        LfeCreateFileEvent event = Mockito.mock(LfeCreateFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        observer.addCreateFileListener(
                e -> notified.set(e == event)
        );
        observer.notifyCreateFileEvent(event);

        assertThat(notified.get()).isTrue();
    }

    @Test
    void removedCreateListeners_areNotNotifiedAnyMore_withCreateEventsTest() {
        LfeCreateFileEvent event = Mockito.mock(LfeCreateFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        Registration registration = observer.addCreateFileListener(
                e -> notified.set(e == event)
        );
        registration.remove();
        observer.notifyCreateFileEvent(event);

        assertThat(notified.get()).isFalse();
    }

    @Test
    void addedOpenListeners_areNotified_withOpenEventsTest() {
        LfeOpenFileEvent event = Mockito.mock(LfeOpenFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        observer.addOpenFileListener(
                e -> notified.set(e == event)
        );
        observer.notifyOpenFileEvent(event);

        assertThat(notified.get()).isTrue();
    }

    @Test
    void removedOpenListeners_areNotNotifiedAnyMore_withOpenEventsTest() {
        LfeOpenFileEvent event = Mockito.mock(LfeOpenFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        Registration registration = observer.addOpenFileListener(
                e -> notified.set(e == event)
        );
        registration.remove();
        observer.notifyOpenFileEvent(event);

        assertThat(notified.get()).isFalse();
    }

    @Test
    void addedCloseListeners_areNotified_withCloseEventsTest() {
        LfeCloseFileEvent event = Mockito.mock(LfeCloseFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        observer.addCloseFileListener(
                e -> notified.set(e == event)
        );
        observer.notifyCloseFileEvent(event);

        assertThat(notified.get()).isTrue();
    }

    @Test
    void removedCloseListeners_areNotNotifiedAnyMore_withCloseEventsTest() {
        LfeCloseFileEvent event = Mockito.mock(LfeCloseFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        Registration registration = observer.addCloseFileListener(
                e -> notified.set(e == event)
        );
        registration.remove();
        observer.notifyCloseFileEvent(event);

        assertThat(notified.get()).isFalse();
    }

    @Test
    void addedSaveListeners_areNotified_withSaveEventsTest() {
        LfeSaveFileEvent event = Mockito.mock(LfeSaveFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        observer.addSaveFileListener(
                e -> notified.set(e == event)
        );
        observer.notifySaveFileEvent(event);

        assertThat(notified.get()).isTrue();
    }

    @Test
    void removedSaveListeners_areNotNotifiedAnyMore_withSaveEventsTest() {
        LfeSaveFileEvent event = Mockito.mock(LfeSaveFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        Registration registration = observer.addSaveFileListener(
                e -> notified.set(e == event)
        );
        registration.remove();
        observer.notifySaveFileEvent(event);

        assertThat(notified.get()).isFalse();
    }

    @Test
    void addedAutosaveListeners_areNotified_withAutosaveEventsTest() {
        LfeSaveFileEvent event = Mockito.mock(LfeSaveFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        observer.addAutosaveFileListener(
                e -> notified.set(e == event)
        );
        observer.notifyAutosaveFileEvent(event);

        assertThat(notified.get()).isTrue();
    }

    @Test
    void removedAutosaveListeners_areNotNotifiedAnyMore_withAutosaveEventsTest() {
        LfeSaveFileEvent event = Mockito.mock(LfeSaveFileEvent.class);
        AtomicBoolean notified = new AtomicBoolean(false);

        Registration registration = observer.addAutosaveFileListener(
                e -> notified.set(e == event)
        );
        registration.remove();
        observer.notifyAutosaveFileEvent(event);

        assertThat(notified.get()).isFalse();
    }

}