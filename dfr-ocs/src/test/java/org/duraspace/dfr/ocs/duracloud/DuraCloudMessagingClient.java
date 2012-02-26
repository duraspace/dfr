package org.duraspace.dfr.ocs.duracloud;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.duraspace.dfr.ocs.it.ITConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.ErrorHandler;

import javax.jms.MessageListener;

/**
 * Convenience class for DuraCloud integration tests that need to work
 * with the message broker.
 */
public class DuraCloudMessagingClient implements ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(
            DuraCloudMessagingClient.class);

    private static final DefaultMessageListenerContainer container =
            new DefaultMessageListenerContainer();
    
    private Throwable lastError;

    /**
     * Creates an instance.
     *
     * @param listener the listener impl to connect to the broker.
     */
    public DuraCloudMessagingClient(MessageListener listener) {
        logger.info("Connecting to message broker");
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(ITConstants.DURACLOUD_BROKER_URL);
        container.setConnectionFactory(factory);
        container.setDestination(new ActiveMQTopic(Constants.INGEST_TOPIC +
                "," + Constants.DELETE_TOPIC));
        container.setMessageListener(listener);
        container.setErrorHandler(this);
        container.start();
        container.initialize();
    }

    /**
     * Releases any resources held by this class.
     * NOTE: This does NOT close the message listener given in the constructor.
     */
    public void close() {
        logger.info("Disconnecting from message broker");
        container.stop();
    }
    
    @Override
    public void handleError(Throwable th) {
        setLastError(th);
    }
    
    public Throwable getLastError() {
        return lastError;
    }
    
    public void setLastError(Throwable th) {
        lastError = th;
    }
}
