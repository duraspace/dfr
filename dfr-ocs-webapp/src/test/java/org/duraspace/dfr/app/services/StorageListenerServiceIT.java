package org.duraspace.dfr.app.services;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.junit.*;
//import org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.*;

/**
 * Integration test of the storage listener service messaging using an
 * container internal broker and a mocked test receiver.
 *
 * DWD: Note this is incomplete. It really does not automatically check
 * the results yet but it serves to provide a driver for messaging tests.
 * It will be completed as the MDP framework is fleshed out.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/StorageListenerServiceIT-context.xml")
public class StorageListenerServiceIT {

    private static final Logger logger =
        LoggerFactory.getLogger(StorageListenerServiceIT.class);

    //@Autowired
    //@Qualifier("storageListenerService")
    //private StorageListenerService service;

    // DWD: Note, even though this an integration test, we may change this
    // to a Mockito mocked object.
    @Autowired
    @Qualifier("messageListener")
    private MockBasicMessageListener messageListener;

    @Autowired
    @Qualifier("connectionFactory")
    private ActiveMQConnectionFactory connectionFactory;

    //@Autowired
    //@Qualifier("listenerContainer")
    //private DefaultMessageListenerContainer listenerContainer;

    @Autowired
    @Qualifier("destination")
    private Destination destination;

    @BeforeClass
    public static void runBeforeClass() {
        System.out.println("Run Before Class");
    }

    @AfterClass
    public static void runAfterClass() {
        System.out.println("Run After Class");
    }

    @Before
    public void runBeforeEveryTest() {
        System.out.println("Run Before Every Test");
    }

    @Test
    public void testListener() {

        Session session;
        MessageProducer producer;

        try {

            Connection connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(null);

            try {
                // If these sends go through a local vm broker URI, they may
                // happen too fast.  Give them a little time.
                Thread.sleep(30);
                sendMessage(session, producer);
                Thread.sleep(30);
                sendMessage(session, producer);
                Thread.sleep(30);
                sendMessage(session, producer);
                Thread.sleep(30);
            } catch (Exception e) {
                logger.info("Failed to send the test message - " + e);
            }

        } catch (JMSException e) {
            logger.info("Failed to create send test connection.");
        }

        // Check the number of messages sent.
        Assert.assertEquals(messageListener.getMessageCounter(), 3);

    }

    public void sendMessage(Session session, MessageProducer producer) {

        try {

            // Create a good DuraCloud message
            MapMessage message = session.createMapMessage();
            message.setJMSMessageID("messageId");
            message.setString("spaceId", "spaceId");
            message.setString("storeId", "storeId");
            message.setString("contentId", "contentId");

            // Tell the producer to send the message
            logger.info("Sending message: " +
                message.hashCode() +
                " : " + Thread.currentThread().getName());

            producer.send(destination, message);

        } catch (JMSException e) {
            logger.info("Unable to send test message");
        }

    }

}
