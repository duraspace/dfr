package org.duraspace.dfr.ocs.core;

import java.io.InputStream;
import java.util.Map;

/**
 * Represents the most basic storage object since repositories may support
 * different semantics. The minimum semantics to be able to work with the DfR
 * middleware. This is modeled as a very simple named graph (the Id) containing
 * consisting two resources, metadata and content. Each resource should be
 * considered a a mechanism to dereference and stream an opaque serialization of
 * the resource. The serializations encode a representation of some content and
 * meta-information (metadata) about the content. The metadata is needed if the
 * content is immutable but may also be used for other purposes. If is the
 * responsibility of DfR or one of its client services to determine
 * how to dereference and deserialize the representation.
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
     * Note: This could also be a stream. It was written for DuraCloud and
     *       almost always one would choose to use simple string name-value
     *       pairs it may too restrictive. Akubra settled on Map as the
     *       least common denominator an has its advantages. DWD
     *
     * @return the metadata, never <code>null</code>.
     * @throws OCSException if an IO or remote error occurs.
     */
    Map<String, String> getMetadata() throws OCSException;

    /**
     * Gets the content. Callers may request this multiple times since it the
     * policy of the server whether the call produces a mutable or immutable
     * representation, and we deserialized may contain mutable or immutable
     * parts.
     *
     * @return the serialization of the representation, or <code>null</code> if
     *         if the representation does not exist.
     * @throws OCSException if an IO or remote error occurs.
     */
    InputStream getContent() throws OCSException;

}
