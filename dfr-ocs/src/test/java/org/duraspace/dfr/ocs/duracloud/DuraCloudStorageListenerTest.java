package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.command.ActiveMQTopic;
import org.duraspace.dfr.ocs.core.EventCapturingProcessor;
import org.duraspace.dfr.ocs.core.FedoraObjectStore;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.core.StorageObjectEventProcessor;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.processing.Processor;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.TextMessage;

/**
 * Unit tests for {@link DuraCloudStorageListener}.
 */
public class DuraCloudStorageListenerTest {
    @Test (expected=NullPointerException.class)
    public void nullMessage() {
        new DuraCloudStorageListener().onMessage(null);
    }
    
    @Test
    public void nonMapMessage() {
        StorageObjectEventProcessor processor =
                EasyMock.createMock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener();
        listener.setProcessor(processor);
        EasyMock.replay(processor);
        listener.onMessage(EasyMock.createMock(TextMessage.class));
        EasyMock.verify(processor);
    }

    @Test
    public void unrecognizedTopic() throws Exception {
        StorageObjectEventProcessor processor =
                EasyMock.createMock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener();
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
    public void jmsExceptionGettingTopic() throws Exception {
        StorageObjectEventProcessor processor =
                EasyMock.createMock(StorageObjectEventProcessor.class);
        DuraCloudStorageListener listener = new DuraCloudStorageListener();
        listener.setProcessor(processor);
        MapMessage message = EasyMock.createMock(MapMessage.class);
        EasyMock.expect(message.getJMSDestination()).andThrow(new JMSException("foo"));
        EasyMock.replay(processor, message);
        listener.onMessage(message);
        EasyMock.verify(processor, message);
    }

    @Test
    public void ingestMessage() throws Exception {
        StorageObjectEvent event = startGoodMessageTest(
                Constants.INGEST_TOPIC, "foo");
        Assert.assertEquals(StorageObjectEvent.Type.CREATED, event.getType());
        StorageObject storageObject = event.getStorageObject();
        Assert.assertEquals("foo", storageObject.getId());
        // TODO: determine what metadata should be here as part of DFR-70
        Assert.assertEquals(0, storageObject.getMetadata().size());
        // TODO: check the content as part of DFR-70
    }

    @Test
    public void deleteMessage() throws Exception {
        StorageObjectEvent event = startGoodMessageTest(
                Constants.DELETE_TOPIC, "foo");
        Assert.assertEquals(StorageObjectEvent.Type.DELETED, event.getType());
        StorageObject storageObject = event.getStorageObject();
        Assert.assertEquals("foo", storageObject.getId());
        Assert.assertEquals(0, storageObject.getMetadata().size());
        // TODO: determine what metadata should be here as part of DFR-70
        Assert.assertNull(storageObject.getContent());
    }
    
    private StorageObjectEvent startGoodMessageTest(String recognizedTopic,
            String contentId) throws Exception {
        EventCapturingProcessor processor = new EventCapturingProcessor();
        DuraCloudStorageListener listener = new DuraCloudStorageListener();
        try {
            listener.setProcessor(processor);
            MapMessage message = EasyMock.createMock(MapMessage.class);
            ActiveMQTopic destination = EasyMock.createMock(ActiveMQTopic.class);
            EasyMock.expect(destination.getTopicName()).andReturn(recognizedTopic);
            EasyMock.expect(message.getJMSDestination()).andReturn(destination);
            EasyMock.expect(message.getJMSMessageID()).andReturn("messageId");
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
