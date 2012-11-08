package org.duraspace.dfr.app.services;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;

import org.junit.*;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.*;
import java.util.concurrent.TimeUnit;

/**
 * Integration test of the storage service messaging using an JMS message
 * source (non-Camel) and a simple test receiver.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/StorageServiceIT-context.xml")
public class StorageServiceIT {

    private static final Logger logger =
        LoggerFactory.getLogger(StorageServiceIT.class);

    @Autowired
    @Qualifier("camelContext")
    private CamelContext context;

    @Autowired
    @Qualifier("connectionFactory")
    private ActiveMQConnectionFactory connectionFactory;

    @Autowired
    @Qualifier("destination")
    private Destination destination;

    @Test
    @DirtiesContext
    public void testListener() {

        // Notify when one message is done
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(1).create();

        Session session;
        MessageProducer producer;

        try {

            Connection connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(null);

            try {

                // If these sends go through a local vm broker URI, they may
                // happen too fast.  Give them a little time.  Use the notifier
                // to wait for Camel to process the message.  Wait at most 10
                // seconds to avoid blocking forever if something goes wrong
                sendMessage(session, producer);
                boolean matches = notify.matches(10, TimeUnit.SECONDS);
                // true means the notifier condition matched (= 1 message is done)
                Assert.assertTrue(matches);

            } catch (Exception e) {
                logger.info("Failed to send the test message - " + e);
            }

        } catch (JMSException e) {
            logger.info("Failed to create send test connection.");
        }

    }

    private void sendMessage(Session session, MessageProducer producer) {

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
