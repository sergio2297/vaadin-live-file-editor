package org.vaadin.addons.sfernandez.lfe;

import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiveFileEditorTest {

    //---- Constants and Definitions ----

    //---- Attributes ----
    private LiveFileEditor editor = new LiveFileEditor(new Span());

    //---- Fixtures ----

    //---- Configuration ----
    @BeforeEach
    void setup() {
        editor = new LiveFileEditor(new Span());
    }

    //---- Methods ----

    //---- Tests ----
//    @Test
//    void autosave_isNotRunning_byDefaultTest() {
//        assertThat(editor.isAutosaveRunning()).isFalse();
//    }
//
//    @Test
//    void afterStartAutosave_itIsRunningTest() {
//        editor.startAutosave();
//
//        assertThat(editor.isAutosaveRunning()).isTrue();
//    }

}