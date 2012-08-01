package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.Export;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import org.duraspace.dfr.ocs.core.OCSException;
//import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Unit tests for {@link FedoraRepository}.
 */
public class FedoraRepositoryTest {

    @Test (expected=NullPointerException.class)
    public void initWithNullClient() {
        new FedoraRepository(null);
    }

/*
    @Test
    public void ingest() throws Exception {
        Assert.assertTrue(simulateIngest(true, 0, null));
    }

    @Test
    public void purge() throws Exception {
        FedoraClient fedoraClient = EasyMock.createMock(FedoraClient.class);
        EasyMock.expect(fedoraClient.execute(
                EasyMock.anyObject(PurgeObject.class))).andReturn(null);
        EasyMock.replay(fedoraClient);
        new FedoraRepository(fedoraClient).purge("test:1", "logMessage");
        EasyMock.verify(fedoraClient);
    }
    
    @Test
    public void export() throws Exception {
        Assert.assertNotNull(simulateExport(true));
    }

    private FedoraObject simulateExport(boolean found) throws Exception {
        FedoraClient fedoraClient = EasyMock.createMock(FedoraClient.class);
        FedoraResponse response = EasyMock.createMock(FedoraResponse.class);
        String foxml = Util.toFOXML(new FedoraObject());
        if (found) {
            EasyMock.expect(response.getEntityInputStream()).andReturn(
                    new ByteArrayInputStream(foxml.getBytes("UTF-8")));
            EasyMock.expect(fedoraClient.execute(
                    EasyMock.anyObject(Export.class))).andReturn(response);
        } else {
            EasyMock.expect(fedoraClient.execute(
                    EasyMock.anyObject(Export.class))).andThrow(
                    new FedoraClientException(404, "message"));
        }
        EasyMock.replay(fedoraClient, response);
        FedoraRepository repo = new FedoraRepository(fedoraClient);
        FedoraObject result = repo.export("test:1");
        EasyMock.verify(fedoraClient, response);
        return result;
    }

    @Test
    public void notFoundOnExport() throws Exception {
        Assert.assertNull(simulateExport(false));
    }

    @Test (expected=OCSException.class)
    public void internalServerErrorOnRequest() throws Exception {
        simulateIngest(false, 500, "message");
    }

    @Test
    public void ingestExisting() throws Exception {
        Assert.assertFalse(simulateIngest(false, 500, "ObjectExistsException"));
    }

    private boolean simulateIngest(boolean success, int code, String message)
            throws Exception {
        FedoraClient fedoraClient = EasyMock.createMock(FedoraClient.class);
        FedoraResponse response = EasyMock.createMock(FedoraResponse.class);
        FedoraObject o = new FedoraObject();
        if (success) {
            EasyMock.expect(fedoraClient.execute(
                    EasyMock.anyObject(Ingest.class))).andReturn(response);
        } else {
            EasyMock.expect(fedoraClient.execute(
                    EasyMock.anyObject(Ingest.class))).andThrow(
                    new FedoraClientException(code, message));
        }
        EasyMock.replay(fedoraClient, response);
        FedoraRepository repo =  new FedoraRepository(fedoraClient);
        boolean result = repo.ingest(o, "logMessage");
        EasyMock.verify(fedoraClient, response);
        return result;
    }
    */

}
