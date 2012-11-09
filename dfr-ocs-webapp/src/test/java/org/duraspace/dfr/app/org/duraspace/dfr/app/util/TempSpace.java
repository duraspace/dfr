package org.duraspace.dfr.app.org.duraspace.dfr.app.util;

import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.NotFoundException;
import org.duraspace.dfr.ocs.duracloud.DuraCloudObjectStoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and removes a temporary DuraStore space for testing.
 */
public class TempSpace {

    private static final Logger logger =
        LoggerFactory.getLogger(TempSpace.class);

    private boolean closed = false;
    private DuraCloudObjectStoreClient sourceStoreClient;
    private ContentStore store;
    private String spaceId;

    public TempSpace(DuraCloudObjectStoreClient sourceStoreClient, String spaceId) {

        logger.debug("Constructing a TempSpace");

        this.spaceId = spaceId;
        this.sourceStoreClient = sourceStoreClient;
        store = sourceStoreClient.getContentStore();

        boolean exists = false;
        int count = 0;
        while (!exists && count < 10) {
            try {
                logger.info("Adding a temp space {}", spaceId);
                if (count == 0) {
                    store.createSpace(spaceId, null);
                    logger.info("Created a temp space {}", spaceId);
                }
                store.getSpaceACLs(spaceId);
                exists = true;
            } catch (NotFoundException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    // NOOP
                }
                count++;
            } catch (ContentStoreException e) {
                close();
                throw new RuntimeException(e);
            }
            if (count == 10) throw new RuntimeException("Temporary space " +
                spaceId + " never got created? Checked 10 times.");
        }

    }

    public void close() {

        if (!closed) {
            try {
                logger.info("Deleting temp space {}", spaceId);
                store.deleteSpace(spaceId);
                sourceStoreClient.getContentStoreManager().logout();
                closed = true;
            } catch (ContentStoreException e) {
                logger.error("Error closing", e);
            }
        }

    }

}
