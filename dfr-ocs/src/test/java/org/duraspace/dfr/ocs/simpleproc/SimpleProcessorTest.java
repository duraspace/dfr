package org.duraspace.dfr.ocs.simpleproc;

import com.github.cwilper.fcrepo.dto.core.*;
import org.duraspace.dfr.ocs.core.MemoryFedoraObjectStore;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.duracloud.DuraCloudStorageObject;
import org.duraspace.dfr.ocs.fedora.FedoraObjectEvent;
import org.mockito.Mockito;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Unit tests for {@link SimpleProcessor}.
 */
public class SimpleProcessorTest {

    private static final String PID_PREFIX = "example.org:";
    private static final String DURASTORE_URL = "http://example.org/durastore";
    private static final String CONTENT_ID = "path/to/content";
    private static final String CONTENT_URL =
            DURASTORE_URL + "/spaceId/path/to/content?storeID=storeId";
    private static final String LABEL = "content";
    private static final String CONTENT_URL_DIGEST =
            "3c3b37e4f9449aba371be1dcd04a2645";
    private static final String FEDORA_PID = PID_PREFIX + CONTENT_URL_DIGEST;
    private static final String GOOD_DATE = "Fri, 24 Feb 2012 11:48:02 UTC";
    private static final long GOOD_DATE_MILLIS = 1330084082000L;

    @Test (expected=NullPointerException.class)
    public void initWithNullPidPrefix() {
        new SimpleProcessor(null, DURASTORE_URL);
    }

    @Test (expected=IllegalArgumentException.class)
    public void initWithEmptyPidPrefix() {
        new SimpleProcessor("", DURASTORE_URL);
    }

    @Test (expected=IllegalArgumentException.class)
    public void initWithNoColonPidPrefix() {
        new SimpleProcessor("no-colon", DURASTORE_URL);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullDuraStoreURL() {
        new SimpleProcessor(PID_PREFIX, null);
    }

    @Test (expected=IllegalArgumentException.class)
    public void initWithEmptyDuraStoreURL() {
        new SimpleProcessor(PID_PREFIX, "");
    }

    @Test (expected=IllegalArgumentException.class)
    public void initWithBadSchemeDuraStoreURL() {
        new SimpleProcessor(PID_PREFIX, "bad-scheme://example.org/");
    }

    @Test
    public void processCreated() {
        processCreated(GOOD_DATE, false);
    }

//    @Test (expected=IllegalArgumentException.class)
//    public void processCreatedWithMissingRequiredMetadata() {
//        processCreated(null, false);
//    }
//
//    @Test (expected=IllegalArgumentException.class)
//    public void processCreatedWithMalformedMetadata() {
//        processCreated("Fry, 42 Feb 2012 31:48:02 XYZ", false);
//    }
//
//    @Test (expected=OCSException.class)
//    public void processCreatedWithPreExistingObject() {
//        processCreated(GOOD_DATE, true);
//    }

    private void processCreated(String modifiedDate, boolean preExisting) {

        // process a CREATED event, which should add it to our memory store
        SimpleProcessor processor = new SimpleProcessor(PID_PREFIX,
                DURASTORE_URL);
        //MemoryFedoraObjectStore fedoraObjectStore =
        //        new MemoryFedoraObjectStore();
        if (preExisting) {
            //fedoraObjectStore.ingest(new FedoraObject().pid(FEDORA_PID),
            //        "logMessage");
        }
        //processor.setFedoraObjectStore(fedoraObjectStore);

        Map<String, String> eventMetadata = new HashMap<String, String>();
        eventMetadata.put("objectId", "content");
        eventMetadata.put("objectType", "objectType");
        eventMetadata.put("collectionId", "si:importedObjects");
        eventMetadata.put("store-id", "storeId");
        eventMetadata.put("space-id", "spaceId");

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("content-checksum", "37b51d194a7513e45b5mockit6f6524f2d51f2");
        metadata.put("content-mimetype", "text/plain");
        metadata.put("content-size", "3");
        if (modifiedDate != null) {
            metadata.put("content-modified", modifiedDate);
        }

        DuraCloudStorageObject storageObject =
            Mockito.mock(DuraCloudStorageObject.class);
        Mockito.when(storageObject.getId()).thenReturn(CONTENT_ID);
        Mockito.when(storageObject.getMetadata()).thenReturn(metadata);

        StorageObjectEvent event = Mockito.mock(StorageObjectEvent.class);
        Mockito.when(event.getMetadata()).thenReturn(eventMetadata);
        Mockito.when(event.getStorageObject()).thenReturn(storageObject);
        Mockito.when(event.getEventType()).thenReturn(
                StorageObjectEvent.EventType.CREATED);
        Mockito.when(event.getEventID()).thenReturn("eventId");

        FedoraObjectEvent fdoEvent = processor.process(event);
        FedoraObject fedoraObject = fdoEvent.getFedoraObject();

        Assert.assertNotNull(fedoraObject);
        // Mockito.verify(event).getId();  Not logging for now
        Mockito.verify(storageObject).getMetadata();
        Mockito.verify(event).getMetadata();

        // Make sure the object made it in and looks as expected.
        //Iterator<String> keys = fedoraObjectStore.getFedoraObjectMap().
        //        keySet().iterator();
        //Assert.assertTrue(keys.hasNext());
        //String key = keys.next();
        //FedoraObject fedoraObject = fedoraObjectStore.getFedoraObjectMap().
        //        get(key);

        Datastream expectedDatastream = new Datastream("CONTENT")
                .controlGroup(ControlGroup.REDIRECT);
        expectedDatastream.addVersion(new Date(GOOD_DATE_MILLIS))
                .label("content")
                .mimeType("text/plain")
                .size(3L)
                .contentLocation(URI.create(CONTENT_URL));

        String inlineXML =
            "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
                "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" " +
                "xmlns:fedora=\"info:fedora/fedora-system:def/relations-external#\" " +
                "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
                "xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" " +
                "xmlns:fedora-model=\"info:fedora/fedora-system:def/model#\">\n";
        inlineXML = inlineXML +
            "<rdf:Description rdf:about=\"info:fedora/" + fedoraObject.pid() + "\">\n";
        inlineXML = inlineXML +
            "<fedora:isMemberOfCollection rdf:resource=\"info:fedora/si:importedObjects\"></fedora:isMemberOfCollection>\n" +
            "<fedora-model:hasModel rdf:resource=\"info:fedora/si:projectCModel\"></fedora-model:hasModel>\n" +
            "</rdf:Description>\n" +
            "</rdf:RDF>";

        Datastream expectedDatastream2 = new Datastream("RELS-EXT")
                .controlGroup(ControlGroup.INLINE_XML);
        try {
            expectedDatastream2.addVersion(new Date(GOOD_DATE_MILLIS))
                .label("RDF Statements about this Object")
                .mimeType("application/rdf+xml")
                .size((long) inlineXML.length())
                .inlineXML(new InlineXML(inlineXML));
        } catch (IOException e) {
            // The equality test will fail.
        }

//        FedoraObject expectedFedoraObject = new FedoraObject()
//                .pid(FEDORA_PID)
//                .label(LABEL)
//                .putDatastream(expectedDatastream2)
//                .putDatastream(expectedDatastream);
//            Assert.assertEquals(expectedFedoraObject, fedoraObject);

            // Note: Log is no longer created here.
            //Assert.assertEquals(
            //    "Ingest requested by DFR Object Creation Service. "
            //    + "Event ID: eventId",
            //    fedoraObjectStore.getLogMessageMap().get(key));

    }

//    @Test
//    public void processDeleted() {
//        processDeleted("storeId");
//    }
//
//    @Test (expected=IllegalArgumentException.class)
//    public void processDeletedWithMissingRequiredMetadata() {
//        processDeleted(null);
//    }

//    @Test (expected=IllegalArgumentException.class)
//    public void processDeletedWithEmptyRequiredMetadata() {
//        processDeleted("");
//    }
//
//    private void processDeleted(String storeId) {
//        SimpleProcessor processor = new SimpleProcessor(
//                PID_PREFIX, DURASTORE_URL);
//        MemoryFedoraObjectStore fedoraObjectStore =
//                new MemoryFedoraObjectStore();
//        processor.setFedoraObjectStore(fedoraObjectStore);
//        StorageObjectEvent event = Mockito.mock(
//                StorageObjectEvent.class);
//        StorageObject storageObject = Mockito.mock(StorageObject.class);
//        Mockito.when(storageObject.getId()).thenReturn(CONTENT_ID);
//        Map<String, String> metadata = new HashMap<String, String>();
//        if (storeId != null) {
//            metadata.put("store-id", storeId);
//        }
//        metadata.put("space-id", "spaceId");
//        Mockito.when(storageObject.getMetadata()).thenReturn(metadata);
//        Mockito.when(event.getStorageObject()).thenReturn(storageObject);
//        Mockito.when(event.getType()).thenReturn(
//                StorageObjectEvent.Type.DELETED);
//        Mockito.when(event.getId()).thenReturn("eventId");
//        fedoraObjectStore.getFedoraObjectMap().put(FEDORA_PID,
//                new FedoraObject());
//        Assert.assertEquals(1, fedoraObjectStore.getFedoraObjectMap().size());
//        processor.process(event);
//        Assert.assertEquals(0, fedoraObjectStore.getFedoraObjectMap().size());
//        Assert.assertEquals(
//                "Purge requested by DFR Object Creation Service. "
//                        + "Event ID: eventId",
//                fedoraObjectStore.getLogMessageMap().get(FEDORA_PID));
//    }

}
