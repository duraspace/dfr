package org.duraspace.dfr.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Provides a basic test webapp JMS message listener for use in testing
 * the ability of the webapp to receive messages.
 */
public class MockBasicMessageListener implements MessageListener {

    private static final Logger logger =
        LoggerFactory.getLogger(MockBasicMessageListener.class);

    private long messageCounter = 0;

    /**
     * Creates an instance.
     */
    public MockBasicMessageListener() {
        logger.debug("Constructed a MockBasicMessagelistener");
    }

    @Override
    public void onMessage(Message message) {

        // If this is a well-formed DuraCloud message?
        if (message instanceof MapMessage) {

            logger.debug("Received a well-formed DuraCloud message");

            try {

                String messageID = message.getJMSMessageID();
                //message.setString("spaceId", "spaceId");
                //message.setString("storeId", "storeId");
                //message.setString("contentId", "contentId");
                logger.debug("Message ID - " + messageID);
                messageCounter = messageCounter + 1;

            } catch (JMSException e) {
                logger.debug("Unable to get message contents");
            }
        } else {
           logger.debug("The message was not a well-formed DuraCloud message");
        }

    }

    /**
     * Returns the number of message received by the listener
     *
     * @return the number of messages received by the listener
     */
    public long getMessageCounter() {
        return messageCounter;
    }

}
