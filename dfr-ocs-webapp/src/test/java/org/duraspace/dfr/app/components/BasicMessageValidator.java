/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.app.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.camel.Message;
import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Validates the format of the JMS test messages.
 */
public class BasicMessageValidator {

    private static final Logger logger =
        LoggerFactory.getLogger(BasicMessageValidator.class);

    private long messageCounter = 0;

    /**
     * Creates an instance.
     */
    public BasicMessageValidator() {
        logger.debug("Constructed a BasicMessageValidator");
    }

    /**
     * Tests if the body of the message is valid.
     *
     * @param message a Camel message body
     */
    public boolean isMessageValid(Message message) {

        logger.info("Message - " + message);

        boolean result = true;

        // If this is a well-formed DuraCloud message?
        if (message.getBody() instanceof Map) {

        logger.debug("Received a well-formed DuraCloud message");


        //try {

            //String JMSMessageID = message.getMessageId();
            String JMSMessageID = message.getHeader("JMSMessageID", String.class);
            //String messageID = message.getJMSMessageID();
            logger.info("Message ID -" + JMSMessageID);
            //message.setString("spaceId", "spaceId");
            //message.setString("storeId", "storeId");
            //message.setString("contentId", "contentId");
            //logger.debug("Message ID - " + messageID);
            //messageCounter = messageCounter + 1;

        //} catch (JMSException e) {
        //    logger.debug("Unable to get message contents");
        //}


        } else {
           logger.debug("The message was not a well-formed DuraCloud message");
        }

        return result;

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
