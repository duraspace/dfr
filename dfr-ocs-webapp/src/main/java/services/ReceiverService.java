package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

/**
 * Created with IntelliJ IDEA.
 * User: ddavis
 * Date: 7/29/12
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReceiverService {

    //private static final Logger logger = LoggerFactory.getLogger(
    //    ReceiverService.class);

    private final SimpleMessageListenerContainer container;

    private Throwable lastError;

    /**
     * Creates an instance.
     *
     * @param container the container impl to connect to the broker.
     */
    public ReceiverService(SimpleMessageListenerContainer container) {
        //logger.info("Connecting to message broker");
        this.container = container;
        //ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        //factory.setBrokerURL(ITConstants.DURACLOUD_BROKER_URL);
        //container.setConnectionFactory(factory);
        //container.setDestination(new ActiveMQTopic(Constants.INGEST_TOPIC +
        //    "," + Constants.DELETE_TOPIC));
        //container.setMessageListener(listener);
        //container.setErrorHandler(this);
        //container.start();
        //container.initialize();
    }

}
