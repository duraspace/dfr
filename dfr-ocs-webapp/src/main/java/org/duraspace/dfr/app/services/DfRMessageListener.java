package org.duraspace.dfr.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * Created with IntelliJ IDEA.
 * User: ddavis
 * Date: 8/6/12
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DfRMessageListener implements MessageListener {

    private Logger logger =
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
