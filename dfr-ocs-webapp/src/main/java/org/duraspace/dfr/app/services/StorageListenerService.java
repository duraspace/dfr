package org.duraspace.dfr.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.ErrorHandler;

/**
 * Implements the service that wraps a storage-related MDP. It supports Spring
 * error handling that may occur during asynchronous execution where it may
 * not be possible to throw the error to the original caller.
 *
 * As written, this is dependent on the Spring JMS framework support.
 */
public class StorageListenerService implements ErrorHandler {

    private static final Logger logger =
        LoggerFactory.getLogger(StorageListenerService.class);

    final private DefaultMessageListenerContainer container;

    private Throwable lastError;

    /**
     * Creates an instance.
     *
     * @param container the listener container impl to connect to the broker.
     */
    public StorageListenerService(DefaultMessageListenerContainer container) {
        logger.debug("Constructing a StorageListenerService");
        this.container = container;
        this.container.setErrorHandler(this);
    }

    public DefaultMessageListenerContainer getContainer() {
        return container;
    }

    /**
     * Releases any resources held by this class.
     * NOTE: This does NOT close the message listener given in the constructor.
     * DWD: This may interfere with HA functionality (or may help it) since
     * many Spring listener containers and the JMS brokers have recovery
     * functionality included in them.
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
