/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
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

    /** If false the client connection is closed */
    private boolean closed = false;

    /** The client that provides access to the DuraStore endpoint */
    private DuraCloudObjectStoreClient sourceStoreClient;

    /** The primary store in which the temporary space is added */
    private ContentStore store;

    /** The unique name for the temporary space */
    private String spaceId;

    /**
     * Creates an instance and also attempts to create the temporary space.
     *
     * @param sourceStoreClient a client to access the storage endpoint
     * @param spaceId a name to use for the temporary space
     */
    public TempSpace(DuraCloudObjectStoreClient sourceStoreClient, String spaceId) {

        logger.debug("Constructing a TempSpace");

        this.spaceId = spaceId;
        this.sourceStoreClient = sourceStoreClient;
        store = sourceStoreClient.getContentStore();

        boolean exists = false;
        int count = 0;
        while (!exists && count < 10) {
            try {
                logger.debug("Adding a temp space {}", spaceId);
                if (count == 0) {
                    store.createSpace(spaceId, null);
                    logger.debug("Created a temp space {}", spaceId);
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

    /**
     * Removes the temporary space and closes the connection to the storage
     * endpoint.
     */
    public void close() {

        if (!closed) {
            try {
                logger.debug("Deleting temp space {}", spaceId);
                store.deleteSpace(spaceId);
                // Note: Deleting the temporary space should always be good
                //       but the logout may not be best since Spring
                //       instantiates the beans and they may be used by other
                //       tests. DWD
                sourceStoreClient.getContentStoreManager().logout();
                closed = true;
            } catch (ContentStoreException e) {
                logger.error("Error closing", e);
            }
        }

    }

}
