package org.vaadin.addons.sfernandez.lfe.events;

import es.sfernandez.library4j.types.DataSize;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;

import static org.assertj.core.api.Assertions.assertThat;

class LfeCloseFileEventTest
        implements LfeOperationEventTest {

    //---- Fixtures ----
    private FileInfo dummyFileInfo() {
        return new FileInfo("file.txt", DataSize.ofBytes(96), "text/plain", "File content");
    }

    //---- Methods ----
    @Override
    public LfeOperationEvent createEventWithoutError() {
        return new LfeCloseFileEvent(dummyFileInfo());
    }

    @Override
    public LfeOperationEvent createEventWithError(LfeError error) {
        return new LfeCloseFileEvent(error);
    }

    //---- Tests ----
    @Test
    void createWithNullFileInfo_returnEmptyFileInfoTest() {
        LfeCloseFileEvent event = new LfeCloseFileEvent((FileInfo) null);

        assertThat(event.fileInfo()).isEmpty();
    }

    @Test
    void createAssignsCorrectly_fileInfoTest() {
        FileInfo fileInfo = dummyFileInfo();

        LfeCloseFileEvent event = new LfeCloseFileEvent(fileInfo);

        assertThat(event.fileInfo()).isPresent();
        assertThat(event.fileInfo().orElseThrow()).isSameAs(fileInfo);
    }
}