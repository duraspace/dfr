package org.duraspace.dfr.ocs.fedora;

//import org.easymock.EasyMock;
import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.yourmediashelf.fedora.client.FedoraClient;
import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.client.request.Export;
import com.yourmediashelf.fedora.client.request.GetObjectProfile;
import com.yourmediashelf.fedora.client.request.Ingest;
import com.yourmediashelf.fedora.client.request.PurgeObject;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import org.duraspace.dfr.ocs.core.OCSException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;

/**
 * Unit tests for {@link FedoraObjectStoreClient}.
 */
public class FedoraObjectStoreClientTest {

    /**
     * Checks for a missing client during setup.
     */
    @Test (expected=NullPointerException.class)
    public void initWithNullClient() {
        new FedoraObjectStoreClient(null);
    }

    /**
     * Checks if a simple ingest operation works correctly
     *
     * @throws Exception  if the simple ingest cannot be handled
     */
    @Test
    public void ingest() throws Exception {
        Assert.assertTrue(simulateIngest(true, 0, null));
    }

    /**
     * Checks if a purge operation works correctly
     *
     * @throws Exception  if the purge operation cannot be handled
     */
    @Test
    public void purge() throws Exception {
        FedoraClient fedoraClient = Mockito.mock(FedoraClient.class);
        //Mockito.when(fedoraClient.execute(
        //        Mockito.any(PurgeObject.class))).thenReturn(null);
        new FedoraObjectStoreClient(fedoraClient).purge("test:1", "logMessage");
        Mockito.verify(fedoraClient).execute(Mockito.any(PurgeObject.class));
    }

    /**
     * Checks is a simple export operation works correctly
     *
     * @throws Exception  if the simple export operation cannot be handled
     */
    @Test
    public void export() throws Exception {
        Assert.assertNotNull(simulateExport(true));
    }

    private FedoraObject simulateExport(boolean found) throws Exception {
        FedoraClient fedoraClient = Mockito.mock(FedoraClient.class);
        FedoraResponse response = Mockito.mock(FedoraResponse.class);
        String foxml = Util.toFOXML(new FedoraObject());

        if (found) {
            Mockito.when(response.getEntityInputStream()).thenReturn(
                    new ByteArrayInputStream(foxml.getBytes("UTF-8")));
            Mockito.when(fedoraClient.execute(
                    Mockito.isA(Export.class))).thenReturn(response);
        } else {
            Mockito.when(fedoraClient.execute(
                    Mockito.isA(Export.class))).thenThrow(
                new FedoraClientException(404, "message"));
        }

        FedoraObjectStoreClient repo = new FedoraObjectStoreClient(fedoraClient);
        FedoraObject result = repo.export("test:1");
        Mockito.verify(fedoraClient).execute(Mockito.isA(Export.class));

        return result;
    }

    /**
     * Test if the export is a non-existent object is handled correctly
     *
     * @throws Exception if export of a non-existent object is not handled
     */
    @Test
    public void notFoundOnExport() throws Exception {
        Assert.assertNull(simulateExport(false));
    }

    /**
     * Tests that an exception is thrown when an ingest fails
     *
     * @throws Exception if the ingest fails (expected)
     */
    @Test (expected=OCSException.class)
    public void internalServerErrorOnRequest() throws Exception {
        simulateIngest(false, 500, "message");
    }

    /**
     * Test whether ingesting an existing object (as determined by PID) is
     * handled.
     *
     * @throws Exception  if the ingest of an existing object is not handled
     */
    @Test
    public void ingestExisting() throws Exception {
        Assert.assertFalse(simulateIngest(false, 500, "ObjectExistsException"));
    }

    private boolean simulateIngest(boolean success, int code, String message)
            throws Exception {
        FedoraClient fedoraClient = Mockito.mock(FedoraClient.class);
        FedoraResponse response = Mockito.mock(FedoraResponse.class);
        FedoraObject o = new FedoraObject();
        // We don't permit ingest of objects via the client that already exist
        // as a workaround for this stage of development. The line below makes
        // Mockito deal with the workaround properly. When the OCS code is
        // refactored we can remove the line below. DWD
        Mockito.when(fedoraClient.execute(
                Mockito.isA(GetObjectProfile.class))).thenReturn(null);

        if (success) {
            Mockito.when(fedoraClient.execute(
                Mockito.isA(Ingest.class))).thenReturn(response);
        } else {
            Mockito.when(fedoraClient.execute(
                Mockito.isA(Ingest.class))).thenThrow(
                new FedoraClientException(code, message));
        }

        FedoraObjectStoreClient repo =  new FedoraObjectStoreClient(fedoraClient);
        boolean result = repo.ingest(o, "logMessage");
        Mockito.verify(fedoraClient).execute(Mockito.isA(Ingest.class));

        return result;
    }

}
