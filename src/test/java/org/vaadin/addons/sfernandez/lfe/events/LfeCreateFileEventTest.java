package org.vaadin.addons.sfernandez.lfe.events;

import es.sfernandez.library4j.types.DataSize;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;

import static org.assertj.core.api.Assertions.assertThat;

class LfeCreateFileEventTest
        implements LfeOperationEventTest {

    //---- Fixtures ----
    private FileInfo dummyFileInfo() {
        return new FileInfo("file.txt", DataSize.ofBytes(96), "text/plain", "File content");
    }

    //---- Methods ----
    @Override
    public LfeOperationEvent createEventWithoutError() {
        return new LfeOpenFileEvent(dummyFileInfo());
    }

    @Override
    public LfeOperationEvent createEventWithError(LfeError error) {
        return new LfeOpenFileEvent(error);
    }

    //---- Tests ----
    @Test
    void createWithNullFileInfo_returnEmptyFileInfoTest() {
        LfeOpenFileEvent event = new LfeOpenFileEvent((FileInfo) null);

        assertThat(event.fileInfo()).isEmpty();
    }

    @Test
    void createAssignsCorrectly_fileInfoTest() {
        FileInfo fileInfo = dummyFileInfo();

        LfeOpenFileEvent event = new LfeOpenFileEvent(fileInfo);

        assertThat(event.fileInfo()).isPresent();
        assertThat(event.fileInfo().orElseThrow()).isSameAs(fileInfo);
    }

}