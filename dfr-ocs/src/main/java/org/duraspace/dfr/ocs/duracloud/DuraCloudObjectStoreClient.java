package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to DuraCloud as an object store.
 *
 * Note: Think about making a Camel component out of this.
 */
public class DuraCloudObjectStoreClient {

    private static final Logger logger =
        LoggerFactory.getLogger(DuraCloudObjectStoreClient.class);

    /** The DuraStore instance where things are stored. */
    private ContentStore contentStore;

    /** The DuraCloud storage client manager servicing this connection. */
    private ContentStoreManager contentStoreManager;

    /**
     * Creates an instance.
     *
     * @param manager the DuraCloud <code>ContentStoreManager</code> to use when
     *                     getting storage object content and metadata, never
     *                     <code>null</code>.
     * @param credential the credential to use for storage provider connections.
     */
    public DuraCloudObjectStoreClient(ContentStoreManager manager,
                                      Credential credential) {

        logger.debug("Constructing a DuraCloudObjectStoreClient");

        if (manager == null || credential == null) {
            throw new NullPointerException();
        }

        this.contentStoreManager = manager;

        try {
            manager.login(credential);
            this.contentStore = manager.getPrimaryContentStore();
        } catch (ContentStoreException e) {
            logger.info("Unable to connect to content store");
        }

    }

    /**
     * Get the DuraCloud object store
     *
     * @return the DuraCloud content store
     */
    public ContentStore getContentStore() {
        return contentStore;
    }

    /**
     * Get the DuraCloud content store manager for this connection
     *
     * @return the DuraCloud content store manager
     */
    public ContentStoreManager getContentStoreManager() {
        return contentStoreManager;
    }

}
