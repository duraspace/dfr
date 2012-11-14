package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStore;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * Called when a JMS message is received indicating new content is available in
 * DuraCloud {@link org.duraspace.dfr.ocs.core.StorageObjectEvent}s. It
 * processes the message and returns a
 * {@link org.duraspace.dfr.ocs.core.StorageObjectEvent}.
 */
public class DuraCloudObjectCreatedMessageTranslator {

    private static final Logger logger = LoggerFactory.getLogger(
            DuraCloudObjectCreatedMessageTranslator.class);

    /** Access endpoint for DuraCloud */
    private ContentStore contentStore;

    /**
     * Creates an instance.
     *
     * @param client the DuraCloud <code>DuraCloudObjectStoreClient</code> to
     *               use when getting storage object content and metadata, never
     *               <code>null</code>.
     */
    public DuraCloudObjectCreatedMessageTranslator(DuraCloudObjectStoreClient client) {

        logger.debug("Constructing a DuraCloudObjectCreatedMessageTranslator");

        // Check required arguments are set.
        if (client == null) {
            throw new NullPointerException();
        }

        this.contentStore = client.getContentStore();
        if (contentStore == null) {
            logger.info("Unable to access content store");
        }

    }

    /**
     * Creates a <code>StorageObjectEvent</code> from the body and headers
     * associated with an incoming object created message.
     *
     * @param message an incoming content created message
     * @return a org.duraspace.dfr.ocs.core.StorageObjectEvent object
     */
    public StorageObjectEvent onMessage(Message message) {

        StorageObjectEvent event = null;

        // DuraCloud only supplies Map messages
        if (message instanceof MapMessage) {

            try {

                // Translate the message into a <code>StorageEventObject<code>
                // Note: There is data duplication in this code that could be
                //       reduced. Also, move strings to constants or properties.
                //       DWD
                String messageId = message.getJMSMessageID();
                logger.debug("Received Message: " + messageId);
                // "EventType" remains for compatibility with older code.
                StorageObjectEvent.EventType type = StorageObjectEvent.EventType.CREATED;

                MapMessage mapMessage = (MapMessage) message;
                logger.debug("Map: " + mapMessage);
                String spaceId = mapMessage.getString("spaceId");
                String storeId = mapMessage.getStringProperty("storeId");

                // The contentId encodes the content object's name as well as
                // the equivalent of a collection path. In DuraCloud, there are
                // no actual collection nodes.
                String contentId = mapMessage.getString("contentId");

                // Create the storage object and the encapsulating event.
                StorageObject storageObject = new DuraCloudStorageObject(
                        contentStore, spaceId, contentId,
                        type == StorageObjectEvent.EventType.DELETED);
                event = new StorageObjectEvent(
                        messageId, type, storageObject);
                event.getMetadata().put("space-id", spaceId);
                event.getMetadata().put("store-id", storeId);
                event.getMetadata().put("objectId", contentId);
                event.getMetadata().put("objectType", "content");

            } catch (JMSException e) {
                logger.warn("Error getting topic for JMS message", e);
            }
        } else if (message == null) {
            throw new NullPointerException();
        } else {
            logger.debug("Ignoring JMS message; not a MapMessage: {}", message);
        }

        return event;
    }

}
