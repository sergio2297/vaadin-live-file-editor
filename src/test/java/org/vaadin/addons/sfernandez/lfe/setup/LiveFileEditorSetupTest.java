package org.vaadin.addons.sfernandez.lfe.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiveFileEditorSetupTest {

    //---- Attributes ----
    private LiveFileEditorSetup setup;

    //---- Configuration ----
    @BeforeEach
    void setup() {
        this.setup = new LiveFileEditorSetup();
    }

    //---- Tests ----
    @Test
    void allFileExtensions_areAllowed_byDefaultTest() {
        LiveFileEditorSetup setup = new LiveFileEditorSetup();

        assertThat(setup.isAllFileExtensionsAllowed()).isTrue();
    }

    @Test
    void allowedFileExtensions_areEmpty_byDefaultTest() {
        LiveFileEditorSetup setup = new LiveFileEditorSetup();

        assertThat(setup.getAllowedFileTypes()).isEmpty();
    }

    @Test
    void allowAllFileExtensions_clearCurrentAllowedFileExtensionsTest() {
        setup.setAllowedFileTypes(new FileType());
        assertThat(setup.getAllowedFileTypes()).isNotEmpty();

        setup.allowAllFileExtensions();

        assertThat(setup.getAllowedFileTypes()).isEmpty();
    }

}