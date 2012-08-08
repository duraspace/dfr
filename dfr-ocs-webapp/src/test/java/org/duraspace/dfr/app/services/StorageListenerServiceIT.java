package org.duraspace.dfr.app.services;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.activemq.benchmark.Producer;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.*;
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
 * Created with IntelliJ IDEA.
 * User: ddavis
 * Date: 7/29/12
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/StorageListenerServiceIT-context.xml")
public class StorageListenerServiceIT {

    private Logger logger =
        LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("storageListenerService")
    private StorageListenerService service;

    //@Autowired
    //@Qualifier("messageListener")
    //private DfRMessageListener listener;

    @Autowired
    @Qualifier("connectionFactory")
    private ActiveMQConnectionFactory connectionFactory;

    @Autowired
    @Qualifier("listenerContainer")
    private DefaultMessageListenerContainer listenerContainer;

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
                Thread.sleep(10);
                sendMessage(session, producer);
                Thread.sleep(10);
                sendMessage(session, producer);
                Thread.sleep(10);
                sendMessage(session, producer);
                Thread.sleep(10);
            } catch (Exception e) {
                logger.info("Failed to send the test message - " + e);
            }

        } catch (JMSException e) {
            logger.info("Failed to create send test connection.");
        }

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
                        message.hashCode()  +
                        " : " + Thread.currentThread().getName());

            producer.send(destination, message);

        } catch (JMSException e) {
            logger.info("Unable to send test message");
        }

    }

}
