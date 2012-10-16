package org.duraspace.dfr.ocs.duracloud;

import org.apache.commons.io.IOUtils;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * Integration tests using a running DuraCloud instance.
 */
public class DuraCloudStorageIT {

    // This test will be revised to use the Camel test framework since the
    // JMS message is receive via its AMQ integration.

//    private static DuraCloudTempSpace tempSpace;
//    private static DuraCloudStorageListener listener;
//
//    @BeforeClass
//    public static void setUp() throws Exception {
//        tempSpace = new DuraCloudTempSpace();
//        listener = new DuraCloudStorageListener(tempSpace.getContentStore());
//        listener.setProcessor(processor);
//    }
//
//    @AfterClass
//    public static void tearDown() {
//        messagingClient.close();
//        listener.close();
//        tempSpace.close();
//    }
//
//    @Test
//    public void addThenDelete() throws Exception {
//        String id = "foo";
//        String content = "bar";
//
//        // add via DuraCloud and wait for event
//        tempSpace.addContent(id, content);
//        StorageObjectEvent event = waitForEvent();
//
//        // check event and storage object
//        Assert.assertEquals(StorageObjectEvent.Type.CREATED, event.getType());
//        StorageObject o = event.getStorageObject();
//        Assert.assertEquals(id, o.getId());
//        // metadata is as expected and can be requested multiple times
//        Map<String, String> metadata = o.getMetadata();
//        Assert.assertEquals(6, metadata.size());
//        metadata = o.getMetadata();
//        Assert.assertEquals(6, metadata.size());
//        Assert.assertEquals(tempSpace.getId(), metadata.get("space-id"));
//        Assert.assertEquals(tempSpace.getContentStore().getStoreId(), metadata.get("store-id"));
//        Assert.assertEquals("37b51d194a7513e45b56f6524f2d51f2", metadata.get("content-checksum"));
//        Assert.assertEquals("text/plain", metadata.get("content-mimetype"));
//        Assert.assertEquals("3", metadata.get("content-size"));
//        Assert.assertNotNull(metadata.get("content-modified"));
//        // content is as expected can be requested multiple times
//        Assert.assertEquals(content, IOUtils.toString(o.getContent()));
//        Assert.assertEquals(content, IOUtils.toString(o.getContent()));
//
//        // delete via DuraCloud and wait for event
//        processor.setLastEvent(null);
//        tempSpace.deleteContent(id);
//        event = waitForEvent();
//
//        // check event and storage object
//        Assert.assertEquals(StorageObjectEvent.Type.DELETED, event.getType());
//        o = event.getStorageObject();
//        Assert.assertEquals(id, o.getId());
//        // metadata is as expected and can be requested multiple times
//        metadata = o.getMetadata();
//        Assert.assertEquals(2, metadata.size());
//        metadata = o.getMetadata();
//        Assert.assertEquals(2, metadata.size());
//        Assert.assertEquals(tempSpace.getId(), metadata.get("space-id"));
//        Assert.assertEquals(tempSpace.getContentStore().getStoreId(), metadata.get("store-id"));
//        // content is as expected can be requested multiple times
//        Assert.assertNull(o.getContent());
//        Assert.assertNull(o.getContent());
//    }
//
//    private StorageObjectEvent waitForEvent() {
//        long msCount = 0;
//        while (processor.getLastEvent() == null && msCount < 5000) {
//            msCount += 50;
//            try { Thread.sleep(50); } catch (InterruptedException e) { }
//        }
//        if (msCount == 5000) {
//            Assert.fail("Waited > 5 seconds and never got event");
//        }
//        return processor.getLastEvent();
//    }

}
