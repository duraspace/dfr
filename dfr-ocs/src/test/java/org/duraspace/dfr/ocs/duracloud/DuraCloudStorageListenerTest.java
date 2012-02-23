package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.command.ActiveMQTopic;
import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duraspace.dfr.ocs.core.EventCapturingProcessor;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.core.StorageObjectEventProcessor;
import org.easymock.EasyMock;
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
        new DuraCloudStorageListener(
                EasyMock.createMock(ContentStore.class)).onMessage(null);
    }
    
    @Test
    public void sendNonMapMessage() {
        StorageObjectEventProcessor processor =
                EasyMock.createMock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener(
                EasyMock.createMock(ContentStore.class));
        listener.setProcessor(processor);
        EasyMock.replay(processor);
        listener.onMessage(EasyMock.createMock(TextMessage.class));
        EasyMock.verify(processor);
    }

    @Test
    public void sendMessageWithUnrecognizedTopic() throws Exception {
        StorageObjectEventProcessor processor =
                EasyMock.createMock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener(
                EasyMock.createMock(ContentStore.class));
        listener.setProcessor(processor);
        MapMessage message = EasyMock.createMock(MapMessage.class);
        ActiveMQTopic destination = EasyMock.createMock(ActiveMQTopic.class);
        EasyMock.expect(destination.getTopicName()).andReturn("foo");
        EasyMock.expect(message.getJMSDestination()).andReturn(destination);
        EasyMock.replay(processor, destination, message);
        listener.onMessage(message);
        EasyMock.verify(processor, destination, message);
    }

    @Test
    public void simulateJmsExceptionGettingTopic() throws Exception {
        StorageObjectEventProcessor processor =
                EasyMock.createMock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener(
                EasyMock.createMock(ContentStore.class));
        listener.setProcessor(processor);
        MapMessage message = EasyMock.createMock(MapMessage.class);
        EasyMock.expect(message.getJMSDestination()).andThrow(new JMSException("foo"));
        EasyMock.replay(processor, message);
        listener.onMessage(message);
        EasyMock.verify(processor, message);
    }

    @Test
    public void sendIngestMessage() throws Exception {
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
        Content content = EasyMock.createMock(Content.class);
        EasyMock.expect(content.getProperties()).andReturn(new HashMap<String, String>());
        EasyMock.expect(content.getStream()).andReturn(new ByteArrayInputStream(new byte[0]));
        EasyMock.expect(contentStore.getContent("spaceId", "contentId")).andReturn(content);
        EasyMock.replay(content, contentStore);
        StorageObjectEvent event = startGoodMessageTest(
                Constants.INGEST_TOPIC, "contentId", contentStore);
        Assert.assertEquals(StorageObjectEvent.Type.CREATED, event.getType());
        StorageObject storageObject = event.getStorageObject();
        
        Assert.assertEquals("contentId", storageObject.getId());
        Assert.assertEquals(2, storageObject.getMetadata().size());
        Assert.assertEquals(-1, storageObject.getContent().read());
        EasyMock.verify(content, contentStore);
    }

    @Test
    public void sendDeleteMessage() throws Exception {
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
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
        DuraCloudStorageListener listener = new DuraCloudStorageListener(
                contentStore);
        try {
            listener.setProcessor(processor);
            MapMessage message = EasyMock.createMock(MapMessage.class);
            ActiveMQTopic destination = EasyMock.createMock(ActiveMQTopic.class);
            EasyMock.expect(destination.getTopicName()).andReturn(recognizedTopic);
            EasyMock.expect(message.getJMSDestination()).andReturn(destination);
            EasyMock.expect(message.getJMSMessageID()).andReturn("messageId");
            EasyMock.expect(message.getString("spaceId")).andReturn("spaceId");
            EasyMock.expect(message.getStringProperty("storeId")).andReturn("storeId");
            EasyMock.expect(message.getString("contentId")).andReturn(contentId);
            EasyMock.replay(destination, message);
            listener.onMessage(message);
            EasyMock.verify(destination, message);
            StorageObjectEvent event = processor.getLastEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals("messageId", event.getId());
            return event;
        } finally {
            listener.close();
        }
    }
}
