package org.vaadin.addons.sfernandez.lfe;

import elemental.json.Json;
import elemental.json.JsonValue;
import es.sfernandez.library4j.types.DataSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.TestAbortedException;
import org.vaadin.addons.sfernandez.lfe.error.LiveFileEditorException;
import org.vaadin.addons.sfernandez.lfe.parameters.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LfeJsParameterHandlerTest {

    //---- Attributes ----
    private final LfeJsParameterHandler handler = new LfeJsParameterHandler();

    //---- Fixtures ----
    private static Stream<Arguments> optionsFilePicker() {
        return Stream.of(
                Arguments.of(new OptionsOpenFile()),
                Arguments.of(new OptionsCreateFile())
        );
    }

    //---- Methods ----
    private String minify(String json) {
        Pattern pattern = Pattern.compile("(\"(?:[^\"\\\\]|\\\\.)*\")|\\s+");
        Matcher matcher = pattern.matcher(json);

        StringBuilder resultado = new StringBuilder();

        int lastEnd = 0;

        while (matcher.find()) {
            // Append everything between last match and current match start
            if (matcher.start() > lastEnd) {
                resultado.append(json, lastEnd, matcher.start());
            }

            if (matcher.group(1) != null) {
                // Es un string: lo dejamos igual
                resultado.append(matcher.group(1));
            }
            // Si no es string, es espacio: no lo añadimos

            lastEnd = matcher.end();
        }

        // Añadir el resto del texto que queda después de la última coincidencia
        if (lastEnd < json.length()) {
            resultado.append(json.substring(lastEnd));
        }

        return resultado.toString();
    }

    private void assertThatJsonContains(JsonValue output, String jsonToContain) {
        assertThat(
                minify(output.toJson())
        ).contains(
                minify(jsonToContain)
        );
    }

    private void assertThatJsonDoesNotContain(JsonValue output, String jsonToNotContain) {
        assertThat(
                minify(output.toJson())
        ).doesNotContain(
                minify(jsonToNotContain)
        );
    }

    private JsonValue mapToJsonUsingHandler(final OptionsHandlingFilePicker options) {
        if(options instanceof OptionsCreateFile createFileOptions) {
            return handler.mapToJson(createFileOptions);
        } else if(options instanceof OptionsOpenFile openFileOptions) {
            return handler.mapToJson(openFileOptions);
        } else {
            throw new TestAbortedException("The give options instance is not handled properly");
        }
    }

    //---- Tests ----
    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withExcludeAllOption_includeJsonPropertyTest(final OptionsHandlingFilePicker options) {
        options.setExcludeAcceptAllOption(true);

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                "\"excludeAcceptAllOption\": true"
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withoutExcludeAllOption_includeJsonPropertyTest(final OptionsHandlingFilePicker options) {
        options.setExcludeAcceptAllOption(false);

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                "\"excludeAcceptAllOption\": false"
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withoutStartInDirectory_doesNotIncludeItTest(final OptionsHandlingFilePicker options) {
        options.setStartIn(null);

        assertThatJsonDoesNotContain(
                mapToJsonUsingHandler(options),
                "\"startIn\":"
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withStartInDirectory_includesItTest(final OptionsHandlingFilePicker options) {
        options.setStartIn(OptionsHandlingFilePicker.WellKnownDirectories.DOCUMENTS);

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                "\"startIn\": \"documents\""
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withNullFileTypes_returnEmptyArrayTest(final OptionsHandlingFilePicker options) {
        options.setAllowedFileTypes(null);

        assertThatJsonDoesNotContain(
                mapToJsonUsingHandler(options),
                "\"types\":"
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withEmptyFileTypes_returnEmptyArrayTest(final OptionsHandlingFilePicker options) {
        options.setAllowedFileTypes(new FileType[0]);

        assertThatJsonDoesNotContain(
                mapToJsonUsingHandler(options),
                "\"types\":"
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withNullFileType_returnEmptyArrayTest(final OptionsHandlingFilePicker options) {
        options.setAllowedFileTypes(new FileType[] { null });

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                "\"types\": []"
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withOneFileType_returnOneFileTypeArrayTest(final OptionsHandlingFilePicker options) {
        FileType[] fileTypes = {
                new FileType("Images", "image/*", ".png", ".jpg", ".gif")
        };
        options.setAllowedFileTypes(fileTypes);

        String jsonToContain = """
                        "types": [
                            {
                                "description": "Images",
                                "accept": {
                                    "image/*": [".png", ".jpg", ".gif"]
                                }
                            }
                        ]
                        """;

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                jsonToContain
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withFileType_withNullDescription_doesNotIncludeDescriptionPropertyTest(final OptionsHandlingFilePicker options) {
        FileType fileTypeWithNullDescription = new FileType(null, "text/plain", ".txt");
        options.setAllowedFileTypes(
                new FileType[] { fileTypeWithNullDescription }
        );

        String jsonToContain = """
                "types": [
                    {
                        "accept": {
                            "text/plain": [".txt"]
                        }
                    }
                ]
                """;

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                jsonToContain
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withFileType_withEmptyDescription_doesNotIncludeDescriptionPropertyTest(final OptionsHandlingFilePicker options) {
        FileType fileTypeWithEmptyDescription = new FileType("   ", "text/plain", ".txt");
        options.setAllowedFileTypes(
                new FileType[] { fileTypeWithEmptyDescription }
        );

        String jsonToContain = """
                "types": [
                    {
                        "accept": {
                            "text/plain": [".txt"]
                        }
                    }
                ]
                """;

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                jsonToContain
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withFileType_withoutMimeType_throwsExceptionTest(final OptionsHandlingFilePicker options) {
        FileType fileTypeWithNullMimeType = new FileType("Text", null, ".txt");
        options.setAllowedFileTypes(
                new FileType[] { fileTypeWithNullMimeType }
        );
        assertThrows(LiveFileEditorException.class, () -> mapToJsonUsingHandler(options));

        FileType fileTypeWithEmptyMimeType = new FileType("Text", "   ", ".txt");
        options.setAllowedFileTypes(
                new FileType[] { fileTypeWithEmptyMimeType }
        );
        assertThrows(LiveFileEditorException.class, () -> mapToJsonUsingHandler(options));
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withFileType_withoutFileExtensions_returnsEmptyAcceptedArrayTest(final OptionsHandlingFilePicker options) {
        FileType[] fileTypes = {
                new FileType("Text", "text/plain")
        };
        options.setAllowedFileTypes(fileTypes);

        String jsonToContain = """
                        "types": [
                            {
                                "description": "Text",
                                "accept": {
                                    "text/plain": []
                                }
                            }
                        ]
                        """;

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                jsonToContain
        );
    }

    @ParameterizedTest
    @MethodSource("optionsFilePicker")
    void mapToJson_optionsFilePicker_withThreeFileTypes_returnThreeFileTypesArrayTest(final OptionsHandlingFilePicker options) {
        FileType[] fileTypes = {
                new FileType("Images", "image/*", ".png", ".jpg", ".gif"),
                new FileType("Text", "text/plain"),
                new FileType("Text", "text/plain", (String[]) null),
                new FileType(null, "application/zip", ".zip")
        };
        options.setAllowedFileTypes(fileTypes);

        String jsonToContain = """
                "types": [
                    {
                        "description": "Images",
                        "accept": {
                            "image/*": [".png", ".jpg", ".gif"]
                        }
                    },
                    {
                        "description": "Text",
                        "accept": {
                            "text/plain": []
                        }
                    },
                    {
                        "description": "Text",
                        "accept": {
                            "text/plain": []
                        }
                    },
                    {
                        "accept": {
                            "application/zip": [".zip"]
                        }
                    }
                ]
                """;

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                jsonToContain
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void mapToJson_createFileOptions_withNullOrEmptySuggestedName_doesNotIncludeItTest(String notValidSuggestedName) {
        OptionsCreateFile options = new OptionsCreateFile();
        options.setSuggestedName(notValidSuggestedName);

        assertThatJsonDoesNotContain(
                mapToJsonUsingHandler(options),
                "\"suggestedName\":"
        );
    }

    @Test
    void mapToJson_createFileOptions_withSuggestedName_includesItTest() {
        OptionsCreateFile options = new OptionsCreateFile();
        options.setSuggestedName("FileName.txt");

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                "\"suggestedName\": \"FileName.txt\""
        );
    }

    @Test
    void mapToJson_openFileOptions_withMultipleSelection_includeJsonPropertyTest() {
        OptionsOpenFile options = new OptionsOpenFile();
        options.setMultipleSelection(true);

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                "\"multiple\": true"
        );
    }

    @Test
    void mapToJson_openFileOptions_withoutMultipleSelection_includeJsonPropertyTest() {
        OptionsOpenFile options = new OptionsOpenFile();
        options.setMultipleSelection(false);

        assertThatJsonContains(
                mapToJsonUsingHandler(options),
                "\"multiple\": false"
        );
    }

    @Test
    void toFileInfo_assignsCorrectly_fileNameTest() {
        JsonValue input = Json.parse(
                """
                {
                    "name": "file.txt",
                    "size": 8000,
                    "type": "text/plain",
                    "content": "This is an example content",
                }
                """
        );

        FileInfo fileInfo = handler.mapToFileInfo(input);

        assertThat(fileInfo.name()).isEqualTo("file.txt");
    }

    @Test
    void toFileInfo_assignsCorrectly_fileSizeTest() {
        JsonValue input = Json.parse(
                """
                {
                    "name": "file.txt",
                    "size": 8000,
                    "type": "text/plain",
                    "content": "This is an example content",
                }
                """
        );

        FileInfo fileInfo = handler.mapToFileInfo(input);

        assertThat(fileInfo.size()).isEqualTo(DataSize.ofBytes(8000));
    }

    @Test
    void toFileInfo_assignsCorrectly_fileTypeTest() {
        JsonValue input = Json.parse(
                """
                {
                    "name": "file.txt",
                    "size": 8000,
                    "type": "text/plain",
                    "content": "This is an example content",
                }
                """
        );

        FileInfo fileInfo = handler.mapToFileInfo(input);

        assertThat(fileInfo.type()).isEqualTo("text/plain");
    }

    @Test
    void toFileInfo_assignsCorrectly_fileContentTest() {
        JsonValue input = Json.parse(
                """
                {
                    "name": "file.txt",
                    "size": 8000,
                    "type": "text/plain",
                    "content": "This is an example content",
                }
                """
        );

        FileInfo fileInfo = handler.mapToFileInfo(input);

        assertThat(fileInfo.content()).isEqualTo("This is an example content");
    }
}