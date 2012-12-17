/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link StorageObject} whose content and metadata is provided by DuraCloud.
 *
 * Note: This class has aspects of a DTO and a DAO that causes some difficulties
 *       but is convenient.  There is also duplication in the data members and
 *       the event metadata to clean up maybe. DWD
 */
public class DuraCloudStorageObject implements StorageObject {

    /** DuraCloud store where the content can be located. */
    private final ContentStore contentStore;

    /** DuraCloud space in which the contain is found. */
    private final String spaceId;

    /** Identifies the content as a slash delimited string. */
    private final String contentId;

    /** Indicates deleted content making it and its metadata inaccessible. */
    private final boolean deleted;

    /** Stream that provides access to the content (bytestream). */
    private InputStream stream;

    /** Metadata associated with the content in DuraCloud. */
    private Map<String, String> metadata;

    /**
     * Creates an instance.
     *
     * @param contentStore the store to use for getting content/metadata.
     * @param spaceId the space id.
     * @param contentId the content id.
     * @param deleted <code>true</code> if the content has been deleted and
     *                therefore the stream and metadata cannot be obtained
     *                from DuraCloud.
     */
    DuraCloudStorageObject(ContentStore contentStore, String spaceId,
            String contentId, boolean deleted) {
        this.contentStore = contentStore;
        this.spaceId = spaceId;
        this.contentId = contentId;
        this.deleted = deleted;
    }

    @Override
    public String getId() {
        return contentId;
    }

    /**
     * Get the content's storage service
     *
     * @return ContentStore the storage service
     */
    public ContentStore getContentStore() {
        return contentStore;
    }

    /**
     * Get the contents space identifier in DuraCloud
     *
     * @return  String the identifier for the space
     */
    public String getSpaceId() {
        return spaceId;
    }

    /**
     * Get the storage object's type, false if deleted
     *
     * @return a
     */
    public boolean getType() {
        return deleted;
    }

    @Override
    public Map<String, String> getMetadata() throws OCSException {
        if (deleted) {
            return new HashMap<>();
        } else if (metadata == null) {
            Content content = getDuraCloudContent();
            metadata = content.getProperties();
            stream = content.getStream();
        }
        return metadata;
    }

    @Override
    public InputStream getContent() throws OCSException {
        if (deleted) {
            return null;
        } else {
            try {
                if (stream != null) {
                    return stream;
                } else {
                    Content content = getDuraCloudContent();
                    metadata = content.getProperties();
                    return content.getStream();
                }
            } finally {
                stream = null;
            }
        }
    }

    /**
     * Ret the bytestream content from the store
     *
     * @return the Content
     * @throws OCSException
     */
    private Content getDuraCloudContent() throws OCSException {
        try {
            return contentStore.getContent(spaceId, contentId);
        } catch (ContentStoreException e) {
            throw new OCSException("Error getting content '"
                    + contentId + "' from space '" + spaceId + "'", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (stream != null) {
            stream.close();
        }
    }

}
