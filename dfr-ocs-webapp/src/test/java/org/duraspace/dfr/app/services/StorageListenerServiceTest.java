package org.duraspace.dfr.app.services;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for the storage listener service.
 *
 * DWD: This test barely has anything in it yet and needs to be extended to
 * cover the MDP.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:/storageListenerServiceIT-context.xml")
public class StorageListenerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(
        StorageListenerServiceTest.class);

    @Test
    public void constructorTest() {

/*
        // Just a placeholder for real tests.
        logger.debug("Performing constructor test");

        DefaultMessageListenerContainer container =
            new DefaultMessageListenerContainer();
        assertNotNull(container);

        StorageListenerService service = new StorageListenerService(container);
        assertNotNull(service);
*/

    }
}
