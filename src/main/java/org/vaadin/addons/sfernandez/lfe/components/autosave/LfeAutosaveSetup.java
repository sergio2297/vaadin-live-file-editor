package org.vaadin.addons.sfernandez.lfe.components.autosave;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * <p>Setup class for {@link LfeAutosave}</p>
 */
public class LfeAutosaveSetup {

    //---- Attributes ----
    /** How often autosave will run */
    private Duration frequency = Duration.ofSeconds(5);
    /** Object that supplies the data to save automatically */
    private Supplier<String> dataToSave = null;
    /** True if change automatically UI's poll interval is allowed */
    private boolean isAllowedToAlterUiPollInterval = false;

    //---- Methods ----
    public Duration getFrequency() {
        return frequency;
    }

    public void setFrequency(Duration frequency) {
        this.frequency = frequency;
    }

    public Supplier<String> dataToSave() {
        return dataToSave;
    }

    public void setDataToSave(Supplier<String> dataToSave) {
        this.dataToSave = dataToSave;
    }

    public void setAllowedToAlterUiPollInterval(boolean allow) {
        this.isAllowedToAlterUiPollInterval = allow;
    }

    /**
     * <p>If true, when the autosave process starts, the AutosaveManagement will modify the UiPollInterval of the current Ui
     * to ensure it is at least as long as the autosave frequency to avoid outdated saves. When the process is stopped, then
     * the previous UiPollInterval is reassigned again.</p>
     * <p>If false, the AutoSaveManagement won't alter de UiPollInterval of the current Ui. This could be problematic
     * because of outdated saves</p>
     * @return true if change automatically UI's poll interval is enabled
     */
    public boolean isAllowedToAlterUiPollInterval() {
        return isAllowedToAlterUiPollInterval;
    }

}
