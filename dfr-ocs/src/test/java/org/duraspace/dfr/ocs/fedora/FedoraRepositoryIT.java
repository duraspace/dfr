package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraCredentials;
import org.duraspace.dfr.ocs.it.ITConstants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration tests for {@link FedoraRepository} using a real running
 * Fedora repository.
 */
public class FedoraRepositoryIT {
    private static FedoraRepository fedoraRepository;
    
    @BeforeClass
    public static void setUp() throws Exception {
        FedoraCredentials credentials = new FedoraCredentials(
                ITConstants.FEDORA_BASE_URL,
                ITConstants.FEDORA_USERNAME,
                ITConstants.FEDORA_PASSWORD);
        FedoraClient client = new FedoraClient(credentials);
        fedoraRepository = new FedoraRepository(client);
    }
    
    @Test
    public void purgeNonExisting() throws Exception {
        fedoraRepository.purge("test:nonExisting", "logMessage");
    }
    
    @Test
    public void exportNonExisting() throws Exception {
        Assert.assertNull(fedoraRepository.export("test:nonExisting"));
    }
    
    @Test
    public void ingestExisting() throws Exception {
        String pid = "test:" + System.currentTimeMillis();
        FedoraObject inObject = new FedoraObject().pid(pid).label("label");
        Assert.assertTrue(fedoraRepository.ingest(inObject, "logMessage"));
        try {
            Assert.assertFalse(fedoraRepository.ingest(inObject, "logMessage"));
        } finally {
            fedoraRepository.purge(pid, "logMessage");
        }
    }
    
    @Test
    public void ingestThenExportThenPurge() throws Exception {
        String pid = "test:" + System.currentTimeMillis();
        FedoraObject inObject = new FedoraObject().pid(pid).label("label");
        fedoraRepository.ingest(inObject, "logMessage");
        FedoraObject outObject = fedoraRepository.export(pid);
        Assert.assertEquals(inObject.pid(), outObject.pid());
        Assert.assertEquals(inObject.label(), outObject.label());
        fedoraRepository.purge(pid, "logMessage");
        Assert.assertNull(fedoraRepository.export(pid));
    }
}
