package org.vaadin.addons.sfernandez.lfe.parameters;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OptionsHandlingFilePickerTest {

    private String generateRandomStringOfNChars(int numOfChars) {
        return new Random().ints('a', 'z' + 1)
                .limit(numOfChars)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    void settingAnIdLargerThan32Characters_throwsExceptionTest() {
        OptionsOpenFile options = new OptionsOpenFile();

        assertThrows(IllegalArgumentException.class, () -> options.setId(generateRandomStringOfNChars(33)));
    }

}