package org.duraspace.dfr.ocs.core;

import java.io.InputStream;
import java.util.Map;

/**
 * A bytestream with an id and associated metadata.
 */
public interface StorageObject {
    /**
     * Gets the id.
     *
     * @return the id, never <code>null</code>.
     */
    String getId();

    /**
     * Gets the metadata.
     *
     * @return the metadata, never <code>null</code>.
     * @throws OCSException if an IO or remote error occurs.
     */
    Map<String, String> getMetadata() throws OCSException;

    /**
     * Gets the content. Callers may request this multiple times.
     *
     * @return the content, possibly zero bytes, never <code>null</code>.
     * @throws OCSException if an IO or remote error occurs.
     */
    InputStream getContent() throws OCSException;
}
