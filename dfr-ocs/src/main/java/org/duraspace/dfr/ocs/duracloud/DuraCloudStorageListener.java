package org.duraspace.dfr.ocs.duracloud;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTopic;
import org.duraspace.dfr.ocs.core.OCSException;
import org.duraspace.dfr.ocs.core.StorageObject;
import org.duraspace.dfr.ocs.core.StorageObjectEvent;
import org.duraspace.dfr.ocs.core.StorageObjectEventListener;
import org.duraspace.dfr.ocs.core.StorageObjectEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
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
    
    private StorageObjectEventProcessor processor;

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
                String id = ((MapMessage) message).getString("contentId");
                StorageObjectEvent event = new StorageObjectEvent(
                        message.getJMSMessageID(),
                        type,
                        getStorageObject(id));
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
    
    private StorageObject getStorageObject(final String id) {
        // TODO: Get one whose content is from DuraStore as part of DFR-70
        return new StorageObject() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public Map<String, String> getMetadata() throws OCSException {
                return new HashMap<String, String>();
            }

            @Override
            public InputStream getContent() throws OCSException {
                return null;
            }
        };
    }
}
