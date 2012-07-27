package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.command.ActiveMQTopic;
import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duraspace.dfr.ocs.core.EventCapturingProcessor;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.core.StorageObjectEventProcessor;
import org.mockito.Mockito;
import org.junit.Assert;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

/**
 * Unit tests for {@link DuraCloudStorageListener}.
 */
public class DuraCloudStorageListenerTest {

    @Test (expected=NullPointerException.class)
    public void initWithNullContentStore() {
        new DuraCloudStorageListener(null);
    }
    
    @Test (expected=NullPointerException.class)
    public void sendNullMessage() {
        new DuraCloudStorageListener(Mockito.mock(ContentStore.class)).onMessage(null);
    }

    @Test
    public void sendNonMapMessage() {
        StorageObjectEventProcessor processor = Mockito.mock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener(Mockito.mock(ContentStore.class));
        listener.setProcessor(processor);
        listener.onMessage(Mockito.mock(TextMessage.class));

        // If the listener receives a non-map message it should never process it.
        Mockito.verify(processor, Mockito.never()).process(Mockito.any(StorageObjectEvent.class));
    }

    @Test
    public void sendMessageWithUnrecognizedTopic() throws Exception {
        StorageObjectEventProcessor processor = Mockito.mock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener(Mockito.mock(ContentStore.class));
        listener.setProcessor(processor);
        MapMessage message = Mockito.mock(MapMessage.class);
        ActiveMQTopic destination = Mockito.mock(ActiveMQTopic.class);
        Mockito.when(destination.getTopicName()).thenReturn("foo");
        Mockito.when(message.getJMSDestination()).thenReturn(destination);
        listener.onMessage(message);

        // If the listener receives a message with the wrong Topic it should never process it.
        Mockito.verify(processor, Mockito.never()).process(Mockito.any(StorageObjectEvent.class));
    }

    @Test
    public void simulateJmsExceptionGettingTopic() throws Exception {
        StorageObjectEventProcessor processor = Mockito.mock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener(Mockito.mock(ContentStore.class));
        listener.setProcessor(processor);
        MapMessage message = Mockito.mock(MapMessage.class);
        Mockito.when(message.getJMSDestination()).thenThrow(new JMSException("foo"));
        listener.onMessage(message);

        // If the listener fails due to a JMSException it should never process it.
        Mockito.verify(processor, Mockito.never()).process(Mockito.any(StorageObjectEvent.class));
    }

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
        Assert.assertEquals(2, storageObject.getMetadata().size());
        Assert.assertEquals(-1, storageObject.getContent().read());
    }

    @Test
    public void sendDeleteMessage() throws Exception {
        ContentStore contentStore = Mockito.mock(ContentStore.class);
        StorageObjectEvent event = startGoodMessageTest(
                Constants.DELETE_TOPIC, "contentId", contentStore);
        Assert.assertEquals(StorageObjectEvent.Type.DELETED, event.getType());
        StorageObject storageObject = event.getStorageObject();
        Assert.assertEquals("contentId", storageObject.getId());
        Assert.assertEquals(2, storageObject.getMetadata().size());
        Assert.assertNull(storageObject.getContent());
    }
    
    private StorageObjectEvent startGoodMessageTest(String recognizedTopic,
            String contentId, ContentStore contentStore) throws Exception {
        EventCapturingProcessor processor = new EventCapturingProcessor();
        EventCapturingProcessor processorSpy = Mockito.spy(processor);
        DuraCloudStorageListener listener = new DuraCloudStorageListener(
                contentStore);
        try {
            listener.setProcessor(processorSpy);
            MapMessage message = Mockito.mock(MapMessage.class);
            ActiveMQTopic destination = Mockito.mock(ActiveMQTopic.class);
            Mockito.when(destination.getTopicName()).thenReturn(recognizedTopic);
            Mockito.when(message.getJMSDestination()).thenReturn(destination);
            Mockito.when(message.getJMSMessageID()).thenReturn("messageId");
            Mockito.when(message.getString("spaceId")).thenReturn("spaceId");
            Mockito.when(message.getStringProperty("storeId")).thenReturn("storeId");
            Mockito.when(message.getString("contentId")).thenReturn(contentId);
            listener.onMessage(message);

            StorageObjectEvent event = processorSpy.getLastEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals("messageId", event.getId());

            // The listener should receive and process a good message.
            Mockito.verify(processorSpy, Mockito.atLeastOnce()).process(Mockito.any(StorageObjectEvent.class));

            return event;
        } finally {
            listener.close();
        }
    }
}
