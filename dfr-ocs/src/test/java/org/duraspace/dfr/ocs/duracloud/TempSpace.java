package org.duraspace.dfr.ocs.duracloud;

import com.sun.corba.se.impl.orbutil.HexOutputStream;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Convenience class for DuraCloud integration tests that need to work with a
 * temporary space.
 */
public class TempSpace {
    private static final String HOST = "dfrtest.duracloud.org";
    private static final String PORT = "443";
    private static final String CONTEXT = "durastore";
    private static final String USER = "dfrtest";
    private static final String PASS = System.getProperty("duracloud.password");
    
    private static final Logger logger =
            LoggerFactory.getLogger(TempSpace.class);

    private final ContentStoreManager contentStoreManager;
    private final ContentStore contentStore;
    private final String id;

    private boolean closed = false;

    public TempSpace() {
        contentStoreManager = new ContentStoreManagerImpl(HOST, PORT, CONTEXT);
        Credential credential = new Credential(USER, PASS);
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
    
    public String getId() {
        return id;
    }
    
    public void addContent(String contentId, String body) {
        try {
            byte[] bytes = body.getBytes("UTF-8");
            InputStream in = new ByteArrayInputStream(bytes);
            contentStore.addContent(id, contentId, in, bytes.length,
                    "text/plain", null, null);
        } catch (ContentStoreException e) {
            throw new RuntimeException("Unexpected error adding content", e);
        } catch (UnsupportedEncodingException wontHappen) {
            throw new RuntimeException(wontHappen);
        }
        
    }
    
    public void deleteContent(String contentId) {
        try {
            contentStore.deleteContent(id, contentId);
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

}
