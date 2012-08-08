package org.duraspace.dfr.app.services;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.*;

/**
 * Created with IntelliJ IDEA.
 * User: ddavis
 * Date: 8/4/12
 * Time: 7:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class DfRMessageProducer {

    private ActiveMQConnectionFactory connectionFactory;
    private JmsTemplate jmsTemplate;
    private Topic topic;


    public DfRMessageProducer(ActiveMQConnectionFactory factory) {
        connectionFactory = factory;
        //this.jmsTemplate = new JmsTemplate(factory);
        //this.topic = new Topic("org.duracloud.topic.change.content.ingest");
    }

    public void sendMessage() {

        System.out.println("Sending a message.");
        try {

            // Create a ConnectionFactory
            //ActiveMQConnectionFactory connectionFactory =
            //    new ActiveMQConnectionFactory("vm://localhost");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session =
                connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination =
                session.createQueue("org.duracloud.topic.change.content.ingest");

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a good message
            TextMessage message = session.createTextMessage("A message.");

            //MapMessage message = session.createMapMessage();
            message.setJMSMessageID("messageId");
            //message.setString("spaceId", "spaceId");
            //message.setString("storeId", "storeId");
            //message.setString("contentId", "contentId");

            // Tell the producer to send the message
            System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
            producer.send(message);

            // Clean up
            //session.close();
            //connection.close();

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

}
