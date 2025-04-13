package org.vaadin.addons.sfernandez.lfe.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.parameters.FileType;

import static org.assertj.core.api.Assertions.assertThat;

class LfeSetupTest {

    //---- Attributes ----
    private LfeSetup setup;

    //---- Configuration ----
    @BeforeEach
    void setup() {
        this.setup = new LfeSetup();
    }

    //---- Tests ----
    @Test
    void allFileTypes_areAllowed_byDefaultTest() {
        LfeSetup setup = new LfeSetup();

        assertThat(setup.isAllFileTypesAllowed()).isTrue();
    }

    @Test
    void allowedFileTypes_areEmpty_byDefaultTest() {
        LfeSetup setup = new LfeSetup();

        assertThat(setup.getAllowedFileTypes()).isEmpty();
    }

    @Test
    void allowAllFileTypes_clearCurrentAllowedFileTypesTest() {
        setup.setAllowedFileTypes(new FileType());
        assertThat(setup.getAllowedFileTypes()).isNotEmpty();

        setup.allowAllFileTypes();

        assertThat(setup.getAllowedFileTypes()).isEmpty();
    }
    
    @Test
    void setAllowedFileTypes_toNull_allowAllFileTypesTest() {
        setup.setAllowedFileTypes((FileType[]) null);

        assertThat(setup.isAllFileTypesAllowed()).isTrue();
    }

}