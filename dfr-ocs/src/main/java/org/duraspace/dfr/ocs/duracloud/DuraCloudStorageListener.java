package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.command.ActiveMQTopic;
import org.duracloud.client.ContentStore;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.core.StorageObjectEventListener;
import org.duraspace.dfr.ocs.core.StorageObjectEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Listens for DuraCloud {@link StorageObjectEvent}s via JMS and sends them
 * to a {@link StorageObjectEventProcessor}.
 */
public class DuraCloudStorageListener
        implements StorageObjectEventListener, MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(
            DuraCloudStorageListener.class);

    private final ContentStore contentStore;

    private StorageObjectEventProcessor processor;

    /**
     * Creates an instance.
     * 
     * @param contentStore the DuraCloud <code>ContentStore</code> to use when
     *                     getting storage object content and metadata, never
     *                     <code>null</code>.
     */
    public DuraCloudStorageListener(ContentStore contentStore) {
        if (contentStore == null) {
            throw new NullPointerException();
        }
        this.contentStore = contentStore;
    }

    @Override
    public void setProcessor(StorageObjectEventProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            try {
                String topic = ((ActiveMQTopic) message.getJMSDestination())
                        .getTopicName();
                StorageObjectEvent.Type type;
                if (topic.equals(Constants.INGEST_TOPIC)) {
                    type = StorageObjectEvent.Type.CREATED;
                } else if (topic.equals(Constants.DELETE_TOPIC)) {
                    type = StorageObjectEvent.Type.DELETED;
                } else {
                    logger.warn("Ignoring message on unrecognized topic: " +
                            "{}, {}", topic, message);
                    return;
                }
                String messageId = message.getJMSMessageID();
                MapMessage mapMessage = (MapMessage) message;
                String spaceId = mapMessage.getString("spaceId");
                String storeId = mapMessage.getStringProperty("storeId");
                String contentId = mapMessage.getString("contentId");
                Map<String, String> messageMetadata =
                        new HashMap<String, String>();
                messageMetadata.put("space-id", spaceId);
                messageMetadata.put("store-id", storeId);
                StorageObject storageObject = new DuraCloudStorageObject(
                        contentStore, spaceId, contentId, messageMetadata,
                        type == StorageObjectEvent.Type.DELETED);
                StorageObjectEvent event = new StorageObjectEvent(
                        messageId, type, storageObject);
                processor.process(event);
            } catch (JMSException e) {
                logger.warn("Error getting topic for JMS message", e);
            }
        } else if (message == null) {
            throw new NullPointerException();
        } else {
            logger.debug("Ignoring JMS message; not a MapMessage: {}", message);
        }
    }
    
}
