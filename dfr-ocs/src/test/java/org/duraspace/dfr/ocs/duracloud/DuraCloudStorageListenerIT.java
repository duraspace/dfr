package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.ocs.core.EventCapturingProcessor;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.core.StorageObjectEventListener;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import java.util.HashMap;
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

    private static final StorageObjectEventListener listener =
            new DuraCloudStorageListener();

    private static final EventCapturingProcessor processor =
            new EventCapturingProcessor();
    
    private static final TempSpace tempSpace = new TempSpace();

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
    public void addAndDeleteContentViaDuraCloud() {
        String id = "foo";
        String content = "bar";

        // add
        tempSpace.addContent(id, "bar");
        StorageObjectEvent event = waitForEvent();
        Assert.assertEquals(StorageObjectEvent.Type.CREATED, event.getType());
        StorageObject o = event.getStorageObject();
        Assert.assertEquals(id, o.getId());
        // TODO: determine what metadata should be here as part of DFR-70
        Assert.assertEquals(0, o.getMetadata().size());
        // TODO: check the content as part of DFR-70

        // delete
        processor.setLastEvent(null);
        tempSpace.deleteContent(id);
        event = waitForEvent();
        Assert.assertEquals(StorageObjectEvent.Type.DELETED, event.getType());
        o = event.getStorageObject();
        Assert.assertEquals(id, o.getId());
        // TODO: determine what metadata should be here as part of DFR-70
        Assert.assertEquals(0, o.getMetadata().size());
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
