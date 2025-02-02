package org.vaadin.addons.sfernandez.lfe.parameters;

import elemental.json.Json;
import elemental.json.JsonValue;
import es.sfernandez.library4j.types.DataSize;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.LiveFileEditorException;
import org.vaadin.addons.sfernandez.lfe.setup.FileType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JsonParameterParserTest {

    //---- Attributes ----
    private final JsonParameterParser parser = new JsonParameterParser();

    //---- Methods ----
    private void testParseAsJson(FileType fileType, JsonValue expectedOutput) {
        testParseAsJson(new FileType[] {fileType}, expectedOutput);
    }

    private void testParseAsJson(FileType[] fileTypes, JsonValue expectedOutput) {
        JsonValue output = parser.asJson(fileTypes);

        assertThat(output.toJson()).isEqualTo(expectedOutput.toJson());
    }

    //---- Tests ----
    @Test
    void asJson_nullFileTypes_returnEmptyArrayTest() {
        testParseAsJson((FileType[]) null, Json.parse("{\"types\": []}"));
    }

    @Test
    void asJson_emptyFileTypes_returnEmptyArrayTest() {
        testParseAsJson(new FileType[0], Json.parse("{\"types\": []}"));
    }

    @Test
    void asJson_oneFileType_returnOneFileTypeArrayTest() {
        FileType fileType = new FileType("Images", "image/*", ".png", ".jpg", ".gif");

        JsonValue expectedOutput = Json.parse(
                """
                {
                    "types": [
                        {
                            "description": "Images",
                            "accept": {
                                "image/*": [".png", ".jpg", ".gif"]
                            }
                        }
                    ]
                }
                """
        );

        testParseAsJson(fileType, expectedOutput);
    }

    @Test
    void asJson_fileType_withoutDescription_doesNotIncludeDescriptionPropertyTest() {
        FileType fileTypeWithNullDescription = new FileType(null, "text/plain", ".txt");
        FileType fileTypeWithEmptyDescription = new FileType("   ", "text/plain", ".txt");

        JsonValue expectedOutput = Json.parse(
                """
                {
                    "types": [
                        {
                            "accept": {
                                "text/plain": [".txt"]
                            }
                        }
                    ]
                }
                """
        );

        testParseAsJson(fileTypeWithNullDescription, expectedOutput);
        testParseAsJson(fileTypeWithEmptyDescription, expectedOutput);
    }

    @Test
    void asJson_fileType_withoutMimeType_throwsExceptionTest() {
        FileType fileTypeWithNullMimeType = new FileType("Text", null, ".txt");
        FileType fileTypeWithEmptyMimeType = new FileType("Text", "   ", ".txt");

        assertThrows(LiveFileEditorException.class, () -> parser.asJson(fileTypeWithNullMimeType));
        assertThrows(LiveFileEditorException.class, () -> parser.asJson(fileTypeWithEmptyMimeType));
    }

    @Test
    void asJson_fileType_withoutFileExtensions_returnsEmptyAcceptedArrayTest() {
        FileType fileType = new FileType("Text", "text/plain");

        JsonValue expectedOutput = Json.parse(
        """
                {
                    "types": [
                        {
                            "description": "Text",
                            "accept": {
                                "text/plain": []
                            }
                        }
                    ]
                }
                """
        );

        testParseAsJson(fileType, expectedOutput);
    }

    @Test
    void asJson_threeFileTypes_returnThreeFileTypesArrayTest() {
        FileType[] fileTypes = {
                new FileType("Images", "image/*", ".png", ".jpg", ".gif"),
                new FileType("Text", "text/plain"),
                new FileType(null, "application/zip", ".zip")
        };

        JsonValue expectedOutput = Json.parse(
                """
                {
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
                            "accept": {
                                "application/zip": [".zip"]
                            }
                        }
                    ]
                }
                """
        );

        testParseAsJson(fileTypes, expectedOutput);
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

        FileInfo fileInfo = parser.toFileInfo(input);

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

        FileInfo fileInfo = parser.toFileInfo(input);

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

        FileInfo fileInfo = parser.toFileInfo(input);

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

        FileInfo fileInfo = parser.toFileInfo(input);

        assertThat(fileInfo.content()).isEqualTo("This is an example content");
    }

    // TODO: Error catching
//    @Test
//    void toFileInfo_assignsCorrectly_fileNameTest() {
//
//    }

}