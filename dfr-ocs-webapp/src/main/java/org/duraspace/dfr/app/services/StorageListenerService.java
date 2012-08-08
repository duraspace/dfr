package org.duraspace.dfr.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.ErrorHandler;

import javax.jms.*;

/**
 * Created with IntelliJ IDEA.
 * User: ddavis
 * Date: 7/29/12
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class StorageListenerService implements ErrorHandler {

    private static final Logger logger =
        LoggerFactory.getLogger(StorageListenerService.class);

    private DefaultMessageListenerContainer container;

    private Throwable lastError;

    /**
     * Creates an instance.
     *
     * @param container the container impl to connect to the broker.
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
