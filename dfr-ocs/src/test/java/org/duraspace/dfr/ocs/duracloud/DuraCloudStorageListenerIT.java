package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.io.IOUtils;
import org.duraspace.dfr.ocs.core.EventCapturingProcessor;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.core.StorageObjectEventListener;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.Map;

/**
 * Integration tests for {@link org.duraspace.dfr.ocs.duracloud.DuraCloudStorageListener}.
 */
public class DuraCloudStorageListenerIT {
    private static final String DURACLOUD_BROKER_URL = "tcp://dfrtest.duracloud.org:61617";

    private static final DefaultMessageListenerContainer container =
            new DefaultMessageListenerContainer();

    private static final Logger logger = LoggerFactory.getLogger(
            DuraCloudStorageListenerIT.class);

    private static final EventCapturingProcessor processor =
            new EventCapturingProcessor();
    
    private static final TempSpace tempSpace = new TempSpace();

    private static final StorageObjectEventListener listener =
            new DuraCloudStorageListener(tempSpace.getContentStore());

    @BeforeClass
    public static void setUp() throws Exception {
        listener.setProcessor(processor);
        logger.info("Connecting to message broker");
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(DURACLOUD_BROKER_URL);
        container.setConnectionFactory(factory);
        container.setDestination(new ActiveMQTopic(Constants.INGEST_TOPIC +
                "," + Constants.DELETE_TOPIC));
        container.setMessageListener(listener);
        container.start();
        container.initialize();
    }
    
    @AfterClass
    public static void tearDown() {
        logger.info("Disconnecting from message broker");
        listener.close();
        container.stop();
        tempSpace.close();
    }

    @Test
    public void addAndDeleteContentViaDuraCloud() throws Exception {
        String id = "foo";
        String content = "bar";

        // add via duracloud and wait for event
        tempSpace.addContent(id, "bar");
        StorageObjectEvent event = waitForEvent();

        // check event and storage object
        Assert.assertEquals(StorageObjectEvent.Type.CREATED, event.getType());
        StorageObject o = event.getStorageObject();
        Assert.assertEquals(id, o.getId());
        // metadata is as expected and can be requested multiple times
        Map<String, String> metadata = o.getMetadata();
        Assert.assertEquals(6, metadata.size());
        metadata = o.getMetadata();
        Assert.assertEquals(6, metadata.size());
        Assert.assertEquals(tempSpace.getId(), metadata.get("space-id"));
        Assert.assertEquals(tempSpace.getContentStore().getStoreId(), metadata.get("store-id"));
        Assert.assertEquals("37b51d194a7513e45b56f6524f2d51f2", metadata.get("content-checksum"));
        Assert.assertEquals("text/plain", metadata.get("content-mimetype"));
        Assert.assertEquals("3", metadata.get("content-size"));
        Assert.assertNotNull(metadata.get("content-modified"));
        // content is as expected can be requested multiple times
        Assert.assertEquals("bar", IOUtils.toString(o.getContent()));
        Assert.assertEquals("bar", IOUtils.toString(o.getContent()));

        // delete via duracloud and wait for event
        processor.setLastEvent(null);
        tempSpace.deleteContent(id);
        event = waitForEvent();

        // check event and storage object
        Assert.assertEquals(StorageObjectEvent.Type.DELETED, event.getType());
        o = event.getStorageObject();
        Assert.assertEquals(id, o.getId());
        // metadata is as expected and can be requested multiple times
        metadata = o.getMetadata();
        Assert.assertEquals(2, metadata.size());
        metadata = o.getMetadata();
        Assert.assertEquals(2, metadata.size());
        Assert.assertEquals(tempSpace.getId(), metadata.get("space-id"));
        Assert.assertEquals(tempSpace.getContentStore().getStoreId(), metadata.get("store-id"));
        // content is as expected can be requested multiple times
        Assert.assertNull(o.getContent());
        Assert.assertNull(o.getContent());
    }
    
    private StorageObjectEvent waitForEvent() {
        long msCount = 0;
        while (processor.getLastEvent() == null && msCount < 5000) {
            msCount += 50;
            try { Thread.sleep(50); } catch (InterruptedException e) { }
        }
        if (msCount == 5000) {
            Assert.fail("Waited > 5 seconds and never got event");
        }
        return processor.getLastEvent();
    }

}
