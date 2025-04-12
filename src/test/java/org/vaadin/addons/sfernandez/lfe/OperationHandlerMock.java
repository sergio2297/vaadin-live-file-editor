package org.vaadin.addons.sfernandez.lfe;

import es.sfernandez.library4j.types.DataSize;
import org.mockito.Mockito;
import org.vaadin.addons.sfernandez.lfe.error.LfeError;
import org.vaadin.addons.sfernandez.lfe.events.LfeCloseFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeCreateFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeOpenFileEvent;
import org.vaadin.addons.sfernandez.lfe.events.LfeSaveFileEvent;
import org.vaadin.addons.sfernandez.lfe.parameters.FileInfo;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class OperationHandlerMock {

    //---- Constants and Definitions ----

    //---- Attributes ----
    private final LfeOperationHandler mock;

    //---- Constructor ----
    public OperationHandlerMock() {
        mock = Mockito.mock(LfeOperationHandler.class);
    }

    //---- Methods ----
    private LfeError dummyError() {
        return new LfeError(LfeError.Type.Other.UNKNOWN, "Unknown error");
    }

    private FileInfo dummyFileInfo() {
        return new FileInfo("file.txt", DataSize.ofBytes(96), "text/plain", "File content");
    }

    public LfeOperationHandler get() {
        return mock;
    }

    public LfeCreateFileEvent mockCreateFileToSuccess() {
        LfeCreateFileEvent successfulEvent = new LfeCreateFileEvent(dummyFileInfo());

        Mockito.when(mock.treatCreateFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(successfulEvent));

        return successfulEvent;
    }

    public LfeCreateFileEvent mockCreateFileToFail() {
        LfeCreateFileEvent unsuccessfulEvent = new LfeCreateFileEvent(dummyError());

        Mockito.when(mock.treatCreateFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(unsuccessfulEvent));

        return unsuccessfulEvent;
    }

    public LfeOpenFileEvent mockOpenFileToSuccess() {
        LfeOpenFileEvent successfulEvent = new LfeOpenFileEvent(dummyFileInfo());

        Mockito.when(mock.treatOpenFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(successfulEvent));

        return successfulEvent;
    }

    public LfeOpenFileEvent mockOpenFileToFail() {
        LfeOpenFileEvent unsuccessfulEvent = new LfeOpenFileEvent(dummyError());

        Mockito.when(mock.treatOpenFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(unsuccessfulEvent));

        return unsuccessfulEvent;
    }

    public LfeCloseFileEvent mockCloseFileToSuccess() {
        LfeCloseFileEvent successfulEvent = new LfeCloseFileEvent(dummyFileInfo());

        Mockito.when(mock.treatCloseFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(successfulEvent));

        return successfulEvent;
    }

    public LfeCloseFileEvent mockCloseFileToFail() {
        LfeCloseFileEvent unsuccessfulEvent = new LfeCloseFileEvent(dummyError());

        Mockito.when(mock.treatCloseFileJsRequest(any()))
                .thenReturn(CompletableFuture.completedFuture(unsuccessfulEvent));

        return unsuccessfulEvent;
    }

    public LfeSaveFileEvent mockSaveFileToSuccess(String content) {
        LfeSaveFileEvent successfulEvent = new LfeSaveFileEvent(content);

        Mockito.when(mock.treatSaveFileJsRequest(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(successfulEvent));

        return successfulEvent;
    }

    public LfeSaveFileEvent mockSaveFileToFail(String content) {
        LfeSaveFileEvent unsuccessfulEvent = new LfeSaveFileEvent(content, dummyError());

        Mockito.when(mock.treatSaveFileJsRequest(any(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(unsuccessfulEvent));

        return unsuccessfulEvent;
    }

}
