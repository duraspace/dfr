package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;

import java.io.InputStream;
import java.util.Map;

/**
 * A {@link StorageObject} whose content and metadata is provided by DuraCloud.
 */
class DuraCloudStorageObject implements StorageObject {
    private final ContentStore contentStore;
    private final String spaceId;
    private final String contentId;
    private final Map<String, String> messageMetadata;
    private final boolean deleted;
    
    private InputStream stream;
    private Map<String, String> metadata;

    /**
     * Creates an instance.
     *
     * @param contentStore the store to use for getting content/metadata.
     * @param spaceId the space id.
     * @param contentId the content id.
     * @param messageMetadata metadata gathered from the JMS message.
     * @param deleted <code>true</code> if the content has been deleted and
     *                therefore the stream and metadata cannot be obtained
     *                from DuraCloud. If this case, only the messageMetadata
     *                will be provided in response to a request for storage
     *                object metadata.
     */
    DuraCloudStorageObject(ContentStore contentStore, String spaceId,
            String contentId, Map<String, String> messageMetadata,
            boolean deleted) {
        this.contentStore = contentStore;
        this.spaceId = spaceId;
        this.contentId = contentId;
        this.messageMetadata = messageMetadata;
        this.deleted = deleted;
    }

    @Override
    public String getId() {
        return contentId;
    }

    @Override
    public Map<String, String> getMetadata() throws OCSException {
        if (deleted) {
            return messageMetadata;
        } else if (metadata == null) {
            Content content = getDuraCloudContent();
            metadata = content.getProperties();
            metadata.putAll(messageMetadata);
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
                    metadata.putAll(messageMetadata);
                    return content.getStream();
                }
            } finally {
                stream = null;
            }
        }
    }

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
