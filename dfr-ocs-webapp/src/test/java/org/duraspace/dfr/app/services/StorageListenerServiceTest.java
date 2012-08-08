package org.duraspace.dfr.app.services;

import org.junit.Test;
import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.JMSException;

/**
 * Created with IntelliJ IDEA.
 * User: ddavis
 * Date: 7/29/12
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class StorageListenerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(
        StorageListenerServiceTest.class);

    @Test
    public void constructorTest() {
        DefaultMessageListenerContainer container =
            new DefaultMessageListenerContainer();
        assertNotNull(container);

        // TODO: Move to Spring Config
        StorageListenerService service = new StorageListenerService(container);
        assertNotNull(service);
    }
}
