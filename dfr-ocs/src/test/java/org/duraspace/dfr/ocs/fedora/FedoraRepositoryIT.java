package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import org.duraspace.dfr.ocs.it.ITConstants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration tests for {@link FedoraObjectStoreClient} using a real running
 * Fedora repository.
 */
public class FedoraRepositoryIT {

    /** A client to interact with a Fedora object store. */
    private static FedoraObjectStoreClient fedoraObjectStoreClient;
    
    @BeforeClass
    public static void setUp() throws Exception {
        FedoraCredentials credentials = new FedoraCredentials(
                ITConstants.FEDORA_BASE_URL,
                ITConstants.FEDORA_USERNAME,
                ITConstants.FEDORA_PASSWORD);
        FedoraClient client = new FedoraClient(credentials);
        fedoraObjectStoreClient = new FedoraObjectStoreClient(client);
    }

    /**
     * Checks whether the purge of a non-existent object is handled silently
     * by the client
     *
     * @throws Exception if the purge of a non-existing object is not handled
     */
    @Test
    public void purgeNonExisting() throws Exception {
        fedoraObjectStoreClient.purge("test:nonExisting", "logMessage");
    }

    /**
     * Checks whether the export of a non-existent object is handled silently
     * by the client
     *
     * @throws Exception if the export of a non-existing object is not handled
     */
    @Test
    public void exportNonExisting() throws Exception {
        Assert.assertNull(fedoraObjectStoreClient.export("test:nonExisting"));
    }

    /**
     * Checks whether you can ingest an object with a PID that is already taken.
     *
     * Note: The client was modified to simply ignore multiple ingests so this
     *       test is OBE. It may change back when we get more sophisticated
     *       rollback handling from Camel.
     * @throws Exception
     */
    @Test
    public void ingestExisting() throws Exception {
        String pid = "test:" + System.currentTimeMillis();
        FedoraObject inObject = new FedoraObject().pid(pid).label("label");
        Assert.assertTrue(fedoraObjectStoreClient.ingest(inObject, "logMessage"));
        try {
            Assert.assertFalse(fedoraObjectStoreClient.ingest(inObject, "logMessage"));
        } finally {
            fedoraObjectStoreClient.purge(pid, "logMessage");
        }
    }

    /**
     * Tests the three basic operations required by the
     * <code>FedoraObjectStore</code> interface.
     *
     * @throws Exception
     */
    @Test
    public void ingestThenExportThenPurge() throws Exception {
        String pid = "test:" + System.currentTimeMillis();
        FedoraObject inObject = new FedoraObject().pid(pid).label("label");
        fedoraObjectStoreClient.ingest(inObject, "logMessage");
        // Note: This is a very weak test. DWD
        FedoraObject outObject = fedoraObjectStoreClient.export(pid);
        Assert.assertEquals(inObject.pid(), outObject.pid());
        Assert.assertEquals(inObject.label(), outObject.label());
        fedoraObjectStoreClient.purge(pid, "logMessage");
        Assert.assertNull(fedoraObjectStoreClient.export(pid));
    }

}
