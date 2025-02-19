package org.vaadin.addons.sfernandez.lfe.components.autosave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

class LfeAutosaveSetupTest {

    //---- Attributes ----
    private LfeAutosaveSetup setup;

    //---- Configuration ----
    @BeforeEach
    void setup() {
        this.setup = new LfeAutosaveSetup();
    }

    //---- Tests ----
    @Test
    void frequency_isFiveSeconds_byDefaultTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup();

        assertThat(setup.getFrequency()).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    void afterSetFrequency_itsValueHasChangedTest() {
        var frequency = Duration.ofSeconds(10);

        setup.setFrequency(frequency);

        assertThat(setup.getFrequency()).isEqualTo(frequency);
    }

    @Test
    void contentToSaveSupplier_isNull_byDefaultTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup();

        assertThat(setup.dataToSave()).isNull();
    }

    @Test
    void afterSetContentToSaveSupplier_itsValueHasChangedTest() {
        Supplier<String> supplier = String::new;

        setup.setDataToSave(supplier);

        assertThat(setup.dataToSave()).isSameAs(supplier);
    }

    @Test
    void alterUiPollInterval_isDisabled_byDefaultTest() {
        LfeAutosaveSetup setup = new LfeAutosaveSetup();

        assertThat(setup.isAllowedToAlterUiPollInterval()).isFalse();
    }

    @Test
    void afterSetEnableAlterUiPollInterval_itsValueHasChangedTest() {
        setup.setAllowedToAlterUiPollInterval(true);

        assertThat(setup.isAllowedToAlterUiPollInterval()).isTrue();
    }

}