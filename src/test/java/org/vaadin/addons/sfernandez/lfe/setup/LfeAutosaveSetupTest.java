package org.vaadin.addons.sfernandez.lfe.setup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.error.LiveFileEditorException;

import java.time.Duration;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LfeAutosaveSetupTest {

    //---- Attributes ----
    private LfeAutosaveSetup.Builder builder;

    //---- Fixtures ----
    private final Supplier<String> fooDataSupplier = String::new;

    //---- Configuration ----
    @BeforeEach
    void setup() {
        this.builder = new LfeAutosaveSetup.Builder()
                .dataToSaveSupplier(fooDataSupplier);
    }

    //---- Tests ----
    @Test
    void frequency_isFiveSeconds_byDefaultTest() {
        LfeAutosaveSetup setup = builder.build();

        assertThat(setup.frequency()).isEqualTo(Duration.ofSeconds(5));
    }

    @Test
    void build_withNullFrequency_throwsExceptionTest() {
        assertThrows(LiveFileEditorException.class, () -> builder.frequency(null).build());
    }

    @Test
    void build_withNegativeOrZeroFrequency_throwsExceptionTest() {
        assertThrows(LiveFileEditorException.class, () -> builder.frequency(Duration.ofMillis(-1)).build());
        assertThrows(LiveFileEditorException.class, () -> builder.frequency(Duration.ofMillis(0)).build());
    }

    @Test
    void build_withNullDataToSaveSupplier_throwsExceptionTest() {
        assertThrows(LiveFileEditorException.class, () -> builder.dataToSaveSupplier(null).build());
    }

    @Test
    void alterUiPollInterval_isNotAllowed_byDefaultTest() {
        LfeAutosaveSetup setup = builder.build();

        assertThat(setup.isAllowedToAlterUiPollInterval()).isFalse();
    }

    @Test
    void build_worksTest() {
        LfeAutosaveSetup setup = builder
                .frequency(Duration.ofSeconds(2))
                .dataToSaveSupplier(fooDataSupplier)
                .allowToAlterUiPollInterval(true)
                .build();

        assertThat(setup.frequency()).isEqualTo(Duration.ofSeconds(2));
        assertThat(setup.dataToSave()).isSameAs(fooDataSupplier);
        assertThat(setup.isAllowedToAlterUiPollInterval()).isTrue();
    }

}