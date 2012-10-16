package org.duraspace.dfr.app.services;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.apache.activemq.spring.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;

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

    //MockStorageListenerService service;

    //CamelContext context = new DefaultCamelContext();

    //@Autowired
    //ConnectionFactory connectionFactory;

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


    /*
    @Test
    public void receiverServiceTest() {
        logger.info("RunningReceiverService");
        service = new MockStorageListenerService();

    }
    */

    /**
     * Creates an instance.
     *
     //* @param container the listener container impl to connect to the broker.
     */
    /*
    class MockStorageListenerService() {
        logger.debug("Constructing a MockStorageListenerService");
        context.addComponent("jms",
            JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    //To change body of implemented methods use File | Settings | File Templates.
                    from("jms:topic:org.duracloud.topic.change.content.ingest").to("file:data/outbox");
                }
            });
        } catch (Exception e) {
            logger.info("Could not create route");
        }
    }
    */

}
