package org.vaadin.addons.sfernandez.lfe.parameters;

import es.sfernandez.library4j.types.DataSize;

/**
 *
 * @param name File name
 * @param size File size in Bytes
 * @param type File MIME type, empty if unknown
 * @param content File content
 */
public record FileInfo(
        String name,
        DataSize size,
        String type,
        String content
) {}
