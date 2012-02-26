package org.duraspace.dfr.ocs.simpleproc;

import com.github.cwilper.fcrepo.dto.core.ControlGroup;
import com.github.cwilper.fcrepo.dto.core.Datastream;
import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import org.duraspace.dfr.ocs.core.MemoryFedoraObjectStore;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

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
    private static final String CONTENT_URL_DIGEST =
            "4d58c9c531d805cad3522d22cb049553";
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

    @Test (expected=IllegalArgumentException.class)
    public void processCreatedWithMissingRequiredMetadata() {
        processCreated(null, false);
    }

    @Test (expected=IllegalArgumentException.class)
    public void processCreatedWithMalformedMetadata() {
        processCreated("Fry, 42 Feb 2012 31:48:02 XYZ", false);
    }
    
    @Test (expected=OCSException.class)
    public void processCreatedWithPreExistingObject() {
        processCreated(GOOD_DATE, true);
    }
    
    private void processCreated(String modifiedDate, boolean preExisting) {
        // process a CREATED event, which should add it to our memory store
        SimpleProcessor processor = new SimpleProcessor(PID_PREFIX,
                DURASTORE_URL);
        MemoryFedoraObjectStore fedoraObjectStore =
                new MemoryFedoraObjectStore();
        if (preExisting) {
            fedoraObjectStore.ingest(new FedoraObject().pid(FEDORA_PID),
                    "logMessage");
        }
        processor.setFedoraObjectStore(fedoraObjectStore);
        StorageObjectEvent event = EasyMock.createMock(
                StorageObjectEvent.class);
        StorageObject storageObject = EasyMock.createMock(StorageObject.class);
        EasyMock.expect(storageObject.getId()).andReturn(CONTENT_ID);
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("store-id", "storeId");
        metadata.put("space-id", "spaceId");
        metadata.put("content-checksum", "37b51d194a7513e45b56f6524f2d51f2");
        metadata.put("content-mimetype", "text/plain");
        metadata.put("content-size", "3");
        if (modifiedDate != null) {
            metadata.put("content-modified", modifiedDate);
        }
        EasyMock.expect(storageObject.getMetadata()).andReturn(metadata);
        EasyMock.expect(event.getStorageObject()).andReturn(storageObject);
        EasyMock.expect(event.getType()).andReturn(
                StorageObjectEvent.Type.CREATED);
        EasyMock.expect(event.getId()).andReturn("eventId");
        EasyMock.replay(event, storageObject);
        processor.process(event);
        Assert.assertEquals(1, fedoraObjectStore.getFedoraObjectMap().size());
        EasyMock.verify(event, storageObject);
        
        // make sure the object made it in and looks as expected
        Iterator<String> keys = fedoraObjectStore.getFedoraObjectMap().
                keySet().iterator();
        Assert.assertTrue(keys.hasNext());
        String key = keys.next();
        FedoraObject fedoraObject = fedoraObjectStore.getFedoraObjectMap().
                get(key);
        Datastream expectedDatastream = new Datastream("CONTENT")
                .controlGroup(ControlGroup.REDIRECT);
        expectedDatastream.addVersion(new Date(GOOD_DATE_MILLIS))
                .label("content")
                .mimeType("text/plain")
                .size(3L)
                .contentLocation(URI.create(CONTENT_URL));
        FedoraObject expectedFedoraObject = new FedoraObject()
                .pid(FEDORA_PID)
                .label(CONTENT_URL)
                .putDatastream(expectedDatastream);
        Assert.assertEquals(expectedFedoraObject, fedoraObject);
        Assert.assertEquals(
                "Ingest requested by DFR Object Creation Service. "
                + "Event ID: eventId",
                fedoraObjectStore.getLogMessageMap().get(key));
    }

    @Test
    public void processDeleted() {
        processDeleted("storeId");
    }

    @Test (expected=IllegalArgumentException.class)
    public void processDeletedWithMissingRequiredMetadata() {
        processDeleted(null);
    }

    @Test (expected=IllegalArgumentException.class)
    public void processDeletedWithEmptyRequiredMetadata() {
        processDeleted("");
    }

    private void processDeleted(String storeId) {
        SimpleProcessor processor = new SimpleProcessor(
                PID_PREFIX, DURASTORE_URL);
        MemoryFedoraObjectStore fedoraObjectStore =
                new MemoryFedoraObjectStore();
        processor.setFedoraObjectStore(fedoraObjectStore);
        StorageObjectEvent event = EasyMock.createMock(
                StorageObjectEvent.class);
        StorageObject storageObject = EasyMock.createMock(StorageObject.class);
        EasyMock.expect(storageObject.getId()).andReturn(CONTENT_ID);
        Map<String, String> metadata = new HashMap<String, String>();
        if (storeId != null) {
            metadata.put("store-id", storeId);
        }
        metadata.put("space-id", "spaceId");
        EasyMock.expect(storageObject.getMetadata()).andReturn(metadata);
        EasyMock.expect(event.getStorageObject()).andReturn(storageObject);
        EasyMock.expect(event.getType()).andReturn(
                StorageObjectEvent.Type.DELETED);
        EasyMock.expect(event.getId()).andReturn("eventId");
        EasyMock.replay(event, storageObject);
        fedoraObjectStore.getFedoraObjectMap().put(FEDORA_PID,
                new FedoraObject());
        Assert.assertEquals(1, fedoraObjectStore.getFedoraObjectMap().size());
        processor.process(event);
        Assert.assertEquals(0, fedoraObjectStore.getFedoraObjectMap().size());
        Assert.assertEquals(
                "Purge requested by DFR Object Creation Service. "
                        + "Event ID: eventId",
                fedoraObjectStore.getLogMessageMap().get(FEDORA_PID));
    }

}
