/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.NotFoundException;
import org.duraspace.dfr.ocs.it.ITConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Convenience class for DuraCloud integration tests that need to work with a
 * temporary space.
 */
public class DuraCloudTempSpace {
    private static final Logger logger =
            LoggerFactory.getLogger(DuraCloudTempSpace.class);

    private final ContentStoreManager contentStoreManager;
    private final ContentStore contentStore;
    private final String id;

    private boolean closed = false;

    public DuraCloudTempSpace() {
        contentStoreManager = new ContentStoreManagerImpl(
                ITConstants.DURACLOUD_HOSTNAME,
                ITConstants.DURACLOUD_PORT, ITConstants.DURASTORE_CONTEXT);
        Credential credential = new Credential(
                ITConstants.DURACLOUD_USERNAME,
                ITConstants.DURACLOUD_PASSWORD);
        contentStoreManager.login(credential);
        try {
            contentStore = contentStoreManager.getPrimaryContentStore();
        } catch (ContentStoreException e) {
            throw new RuntimeException(e);
        }
        id = "temp" + System.currentTimeMillis();
        logger.info("Creating temp space {}", id);
        boolean exists = false;
        int count = 0;
        while (!exists && count < 10) {
            try {
                if (count == 0) contentStore.createSpace(id, null);
                contentStore.getSpaceACLs(id);
                exists = true;
            } catch (NotFoundException e) {
                try { Thread.sleep(1000); } catch (InterruptedException ie) { }
                count++;
            } catch (ContentStoreException e) {
                close();
                throw new RuntimeException(e);
            }
            if (count == 10) throw new RuntimeException("Temporary space " +
                    id + " never got created? Checked 10 times.");
        }
    }

    public ContentStore getContentStore() {
        return contentStore;
    }
    
    public String getId() {
        return id;
    }
    
    public void addContent(String contentId, String body) {
        try {
            byte[] bytes = body.getBytes("UTF-8");
            InputStream in = new ByteArrayInputStream(bytes);
            contentStore.addContent(id, contentId, in, bytes.length,
                    "text/plain", null, null);
            giveAmazonSomeTime();
        } catch (ContentStoreException e) {
            throw new RuntimeException("Unexpected error adding content", e);
        } catch (UnsupportedEncodingException wontHappen) {
            throw new RuntimeException(wontHappen);
        }
        
    }
    
    public void deleteContent(String contentId) {
        try {
            contentStore.deleteContent(id, contentId);
            giveAmazonSomeTime();
        } catch (ContentStoreException e) {
            throw new RuntimeException("Unexpected error deleting content", e);
        }
    }

    public void close() {
        if (!closed) {
            try {
                logger.info("Deleting temp space {}", id);
                contentStore.deleteSpace(id);
                contentStoreManager.logout();
                closed = true;
            } catch (ContentStoreException e) {
                logger.error("Error closing", e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }
    
    private static void giveAmazonSomeTime() {
        // Some S3 regions don't offer read-after-write consistency,
        // meaning that when a write call returns, you're not guaranteed
        // that an immediate subsequent read will reflect the desired state
        // of the space. Eventually it will, and it's almost always within
        // a few seconds, so here we wait and hope subsequent calls to
        // DuraCloud will reflect the state we expect.
        try { Thread.sleep(5000); } catch (InterruptedException e) { }
    }

}
