package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.command.ActiveMQTopic;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.junit.Test;
import org.mockito.Mockito;
import org.junit.Assert;
import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

/**
 * Unit test for <code>DuraCloudCreateProcessor</code>
 */
public class DuraCloudCreateProcessorTest {

    @Test(expected=NullPointerException.class)
    public void initWithNullContentStoreClient() {
        new DuraCloudCreateProcessor(null);
    }

    @Test (expected=NullPointerException.class)
    public void sendNullMessage() {
        new DuraCloudCreateProcessor(Mockito.mock(DuraCloudContentStoreClient.class)).onMessage(null);
    }

    @Test
    public void sendNonMapMessage() {
        DuraCloudCreateProcessor processor =
            new DuraCloudCreateProcessor(Mockito.mock(DuraCloudContentStoreClient.class));

        // If the processor receives a non-map message it should never process it.
        Assert.assertNull(processor.onMessage(Mockito.mock(TextMessage.class)));
    }

//    @Test
//    public void simulateJmsExceptionGettingID() throws Exception {
//        DuraCloudCreateProcessor processor =
//            new DuraCloudCreateProcessor(Mockito.mock(DuraCloudContentStoreClient.class));
//        MapMessage message = Mockito.mock(MapMessage.class);
//        Mockito.when(message.getString("spaceId")).thenThrow(new JMSException("foo"));
//        processor.onMessage(message);
//
//        // If the listener fails due to a JMSException it should never process it.
//        //Mockito.verify(processor, Mockito.never()).process(Mockito.any(StorageObjectEvent.class));
//    }

    @Test
    public void sendIngestMessage() throws Exception {
        ContentStore contentStore = Mockito.mock(ContentStore.class);
        Content content = Mockito.mock(Content.class);
        Mockito.when(content.getProperties()).thenReturn(new HashMap<String, String>());
        Mockito.when(content.getStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        Mockito.when(contentStore.getContent("spaceId", "contentId")).thenReturn(content);
        StorageObjectEvent event = startGoodMessageTest(
                Constants.INGEST_TOPIC, "contentId", contentStore);
        Assert.assertEquals(StorageObjectEvent.Type.CREATED, event.getType());
        StorageObject storageObject = event.getStorageObject();

        Assert.assertEquals("contentId", storageObject.getId());
        //Assert.assertEquals(2, storageObject.getMetadata().size());
        //Assert.assertEquals(-1, storageObject.getContent().read());
    }

    private StorageObjectEvent startGoodMessageTest(String recognizedTopic,
            String contentId, ContentStore contentStore) throws Exception {
        DuraCloudCreateProcessor processor =
            new DuraCloudCreateProcessor(Mockito.mock(DuraCloudContentStoreClient.class));
        MapMessage message = Mockito.mock(MapMessage.class);
        ActiveMQTopic destination = Mockito.mock(ActiveMQTopic.class);
        Mockito.when(destination.getTopicName()).thenReturn(recognizedTopic);
        Mockito.when(message.getJMSDestination()).thenReturn(destination);
        Mockito.when(message.getJMSMessageID()).thenReturn("messageId");
        Mockito.when(message.getString("spaceId")).thenReturn("spaceId");
        Mockito.when(message.getStringProperty("storeId")).thenReturn("storeId");
        Mockito.when(message.getString("contentId")).thenReturn(contentId);

        StorageObjectEvent event = processor.onMessage(message);
        Assert.assertNotNull(event);
        Assert.assertEquals("messageId", event.getId());

        return event;
    }

}
