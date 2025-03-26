package org.vaadin.addons.sfernandez.lfe.setup;

import org.vaadin.addons.sfernandez.lfe.LfeAutosave;
import org.vaadin.addons.sfernandez.lfe.error.LiveFileEditorException;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * <p>Setup class for {@link LfeAutosave}</p>
 */
public class LfeAutosaveSetup {

    //---- Attributes ----
    private final Duration frequency;
    private final Supplier<String> dataToSave;
    private final boolean isAllowedToAlterUiPollInterval;

    //---- Constructor ----
    private LfeAutosaveSetup(Duration frequency, Supplier<String> dataToSave, boolean isAllowedToAlterUiPollInterval) {
        this.frequency = frequency;
        this.dataToSave = dataToSave;
        this.isAllowedToAlterUiPollInterval = isAllowedToAlterUiPollInterval;
    }

    //---- Methods ----
    /**
     * @return a Duration object representing how often autosave will run
     */
    public Duration frequency() {
        return frequency;
    }

    /**
     * @return an object that supplies the data to save automatically
     */
    public Supplier<String> dataToSave() {
        return dataToSave;
    }

    /**
     * <p>If true, when the autosave process starts, the UI's poll interval will be
     * modified by the {@link LfeAutosave} component to ensure that the save operations
     * are executed inside the autosave frequency, that way outdated saves will be prevented.
     * When the process is stopped, then UI's poll interval will be restored.</p>
     * <p>If false, some outdated saves may occur.</p>
     * @return true if change automatically UI's poll interval is allowed
     */
    public boolean isAllowedToAlterUiPollInterval() {
        return isAllowedToAlterUiPollInterval;
    }

    /* *************************************
     *              BUILDER
     * *************************************/
    public static class Builder {

        //---- Attributes ----
        private Duration frequency = Duration.ofSeconds(5);
        private Supplier<String> dataToSaveSupplier = null;
        private boolean isAllowedToAlterUiPollInterval = false;

        //---- Methods ----
        /**
         * <p>Default: 5 seconds</p>
         * @param frequency how often autosave will run
         * @return the Builder
         */
        public Builder frequency(Duration frequency) {
            this.frequency = frequency;
            return this;
        }

        /**
         * @param supplier object that supplies the data to save automatically
         * @return the Builder
         */
        public Builder dataToSaveSupplier(Supplier<String> supplier) {
            this.dataToSaveSupplier = supplier;
            return this;
        }

        /**
         * <p>If true, when the autosave process starts, the UI's poll interval will be
         * modified by the {@link LfeAutosave} component to ensure that the save operations
         * are executed inside the autosave frequency, that way outdated saves will be prevented.
         * When the process is stopped, then UI's poll interval will be restored.</p>
         * <p>If false, some outdated saves may occur.</p>
         * <p>Default: false</p>
         * @param allow true if change automatically UI's poll interval is allowed
         * @return the Builder
         */
        public Builder allowToAlterUiPollInterval(boolean allow) {
            this.isAllowedToAlterUiPollInterval = allow;
            return this;
        }

        /**
         * @return a new {@link LfeAutosaveSetup}
         * @throws LiveFileEditorException if frequency or dataToSaveSupplier are null
         */
        public LfeAutosaveSetup build() {
            validateBuilding();

            return new LfeAutosaveSetup(frequency, dataToSaveSupplier, isAllowedToAlterUiPollInterval);
        }

        private void validateBuilding() {
            if(frequency == null)
                throw new LiveFileEditorException("Error. Autosave frequency must not be null.");

            if(frequency.isNegative() || frequency.isZero())
                throw new LiveFileEditorException("Error. Frequency must be positive.");

            if(dataToSaveSupplier == null)
                throw new LiveFileEditorException("Error. A data to save supplier is mandatory.");
        }
    }

}
