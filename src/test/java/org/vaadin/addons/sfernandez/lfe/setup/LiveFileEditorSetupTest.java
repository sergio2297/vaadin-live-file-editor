package org.vaadin.addons.sfernandez.lfe.setup;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiveFileEditorSetupTest {

    //---- Tests ----
    @Test
    void allFileExtensions_areAllowedByDefaultTest() {
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
        LiveFileEditorSetup setup = new LiveFileEditorSetup();
        setup.setAllowedFileTypes(new FileType());
        assertThat(setup.getAllowedFileTypes()).isNotEmpty();

        setup.allowAllFileExtensions();

        assertThat(setup.getAllowedFileTypes()).isEmpty();
    }

}