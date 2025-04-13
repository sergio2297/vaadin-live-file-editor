package org.vaadin.addons.sfernandez.lfe;

import elemental.json.Json;
import elemental.json.JsonValue;
import es.sfernandez.library4j.types.DataSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.vaadin.addons.sfernandez.lfe.error.LiveFileEditorException;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;
import org.vaadin.addons.sfernandez.lfe.setup.FileType;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LfeJsParameterHandlerTest {

    //---- Attributes ----
    private final LfeJsParameterHandler handler = new LfeJsParameterHandler();

    //---- Fixtures ----
    private final String aString = "String";
    private final FileType aFileType = new FileType("Images", "image/*", ".png", ".jpg", ".gif");

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

    private void assertThatJsonContains(Supplier<JsonValue> jsonGeneration, String jsonToContain) {
        JsonValue output = jsonGeneration.get();

        assertThat(
                minify(output.toJson())
        ).contains(
                minify(jsonToContain)
        );
    }

    private void assertThatJsonDoesNotContain(Supplier<JsonValue> jsonGeneration, String jsonToNotContain) {
        JsonValue output = jsonGeneration.get();

        assertThat(
                minify(output.toJson())
        ).doesNotContain(
                minify(jsonToNotContain)
        );
    }

    //---- Tests ----
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void mapTo_createFileRequest_withNullOrEmptySuggestedName_doesNotIncludeItTest(String notValidSuggestedName) {
        assertThatJsonDoesNotContain(
                () -> handler.mapToCreateFileRequest(notValidSuggestedName, aFileType),
                "\"suggestedName\":"
        );
    }

    @Test
    void mapTo_createFileRequest_withSuggestedName_includesItTest() {
        assertThatJsonContains(
                () -> handler.mapToCreateFileRequest("FileName.txt", aFileType),
                "\"suggestedName\": \"FileName.txt\""
        );
    }

    @Test
    void mapTo_createFileRequest_withNullFileTypes_returnEmptyArrayTest() {
        assertThatJsonDoesNotContain(
                () -> handler.mapToCreateFileRequest(aString, (FileType[]) null),
                "\"types\":"
        );
    }

    @Test
    void mapTo_createFileRequest_withEmptyFileTypes_returnEmptyArrayTest() {
        assertThatJsonDoesNotContain(
                () -> handler.mapToCreateFileRequest(aString),
                "\"types\":"
        );
    }

    @Test
    void mapTo_createFileRequest_withNullFileType_returnEmptyArrayTest() {
        assertThatJsonContains(
                () -> handler.mapToCreateFileRequest(aString, (FileType) null),
                "\"types\": []"
        );
    }

    @Test
    void mapTo_createFileRequest_withOneFileType_returnOneFileTypeArrayTest() {
        FileType fileType = new FileType("Images", "image/*", ".png", ".jpg", ".gif");
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
                () -> handler.mapToCreateFileRequest(aString, fileType),
                jsonToContain
        );
    }


    @Test
    void mapTo_createFileRequest_withFileType_withoutDescription_doesNotIncludeDescriptionPropertyTest() {
        FileType fileTypeWithNullDescription = new FileType(null, "text/plain", ".txt");
        FileType fileTypeWithEmptyDescription = new FileType("   ", "text/plain", ".txt");

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
                () -> handler.mapToCreateFileRequest(aString, fileTypeWithNullDescription),
                jsonToContain
        );
        assertThatJsonContains(
                () -> handler.mapToCreateFileRequest(aString, fileTypeWithEmptyDescription),
                jsonToContain
        );
    }


    @Test
    void mapTo_createFileRequest_withFileType_withoutMimeType_throwsExceptionTest() {
        FileType fileTypeWithNullMimeType = new FileType("Text", null, ".txt");
        FileType fileTypeWithEmptyMimeType = new FileType("Text", "   ", ".txt");

        assertThrows(LiveFileEditorException.class, () -> handler.mapToCreateFileRequest(aString, fileTypeWithNullMimeType));
        assertThrows(LiveFileEditorException.class, () -> handler.mapToCreateFileRequest(aString, fileTypeWithEmptyMimeType));
    }


    @Test
    void mapTo_createFileRequest_withfileType_withoutFileExtensions_returnsEmptyAcceptedArrayTest() {
        FileType fileType = new FileType("Text", "text/plain");
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
                () -> handler.mapToCreateFileRequest(aString, fileType),
                jsonToContain
        );
    }

    @Test
    void mapTo_createFileRequest_withThreeFileTypes_returnThreeFileTypesArrayTest() {
        FileType[] fileTypes = {
                new FileType("Images", "image/*", ".png", ".jpg", ".gif"),
                new FileType("Text", "text/plain"),
                new FileType("Text", "text/plain", (String[]) null),
                new FileType(null, "application/zip", ".zip")
        };

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
                () -> handler.mapToCreateFileRequest(aString, fileTypes),
                jsonToContain
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