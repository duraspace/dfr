package org.duraspace.dfr.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Provides a webapp JMS message listener.
 *
 * DWD: This is to be replaced by the one in dfr-ocs and will move to the test
 * code.
 */
public class DfRMessageListener implements MessageListener {

    private static final Logger logger =
        LoggerFactory.getLogger(DfRMessageListener.class);

    public DfRMessageListener() {
        logger.debug("Constructed a DfRMessagelistener");
    }

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

            } catch (JMSException e) {
                logger.debug("Unable to get message contents");
            }
        } else {
           logger.debug("The message was not a well-formed DuraCloud message");
        }

    }

}
