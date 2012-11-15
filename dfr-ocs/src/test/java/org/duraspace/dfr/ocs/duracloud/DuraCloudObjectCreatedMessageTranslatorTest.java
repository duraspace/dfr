package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.command.ActiveMQTopic;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.junit.Test;
import org.mockito.Mockito;
import org.junit.Assert;
import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;

import javax.jms.MapMessage;
import javax.jms.TextMessage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

/**
 * Unit test for <code>DuraCloudObjectCreatedMessageTranslator</code>
 */
public class DuraCloudObjectCreatedMessageTranslatorTest {

    /**
     * Tests attempt to construct with no content store client.
     */
    @Test(expected=NullPointerException.class)
    public void initWithNullContentStoreClient() {
        new DuraCloudObjectCreatedMessageTranslator(null);
    }

    /**
     * Tests attempt to construct with no message translator.
     */
    @Test (expected=NullPointerException.class)
    public void sendNullMessage() {
        new DuraCloudObjectCreatedMessageTranslator(Mockito.mock(DuraCloudObjectStoreClient.class)).onMessage(null);
    }

    /**
     * DuraCloud only generates map messages.
     */
    @Test
    public void sendNonMapMessage() {
        DuraCloudObjectCreatedMessageTranslator processor =
            new DuraCloudObjectCreatedMessageTranslator(Mockito.mock(DuraCloudObjectStoreClient.class));

        // If the processor receives a non-map message it should never process it.
        Assert.assertNull(processor.onMessage(Mockito.mock(TextMessage.class)));
    }

    ///**
    // * Test situations where JMSExceptions are thrown.
    // *
    // * Note: I have not be able to demonstrate a situation that throws a JMS
    // *       exception since the introduction oF Camel, It captures a number
    // *       of exceptions to be handled by its more capable error handling
    // *       feature set (though I am not sure this is happening in this case
    // *       yet). DWD
    // */
    //    @Test (expected=JMSException.class)
    //    public void simulateJmsExceptionGettingID() {
    //        DuraCloudObjectCreatedMessageTranslator processor =
    //            new DuraCloudObjectCreatedMessageTranslator(Mockito.mock(DuraCloudObjectStoreClient.class));
    //        MapMessage message = Mockito.mock(MapMessage.class);
    //        Mockito.when(message.getString("spaceId")).thenThrow(new JMSException("foo"));
    //        processor.onMessage(message);
    //
    //        // If the listener fails due to a JMSException it should never process it.
    //        //Mockito.verify(processor, Mockito.never()).process(Mockito.any(StorageObjectEvent.class));
    //    }

    @Test
    public void sendIngestMessage() throws Exception {
        //String contentId = "collection/contentId";
        String contentId = "contentId";
        ContentStore contentStore = Mockito.mock(ContentStore.class);
        Content content = Mockito.mock(Content.class);
        Mockito.when(content.getProperties()).thenReturn(new HashMap<String, String>());
        Mockito.when(content.getStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        Mockito.when(contentStore.getContent("spaceId", "contentId")).thenReturn(content);
        StorageObjectEvent event = startGoodMessageTest(
                Constants.INGEST_TOPIC, contentId, contentStore);
        Assert.assertEquals(StorageObjectEvent.EventType.CREATED, event.getEventType());
        StorageObject storageObject = event.getStorageObject();

        Assert.assertEquals(contentId, storageObject.getId());
        Assert.assertEquals(4, event.getMetadata().size());
    }

    private StorageObjectEvent startGoodMessageTest(String recognizedTopic,
            String contentId, ContentStore contentStore) throws Exception {
        DuraCloudObjectCreatedMessageTranslator processor =
            new DuraCloudObjectCreatedMessageTranslator(Mockito.mock(DuraCloudObjectStoreClient.class));
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
        Assert.assertEquals("messageId", event.getEventID());

        return event;
    }

}
