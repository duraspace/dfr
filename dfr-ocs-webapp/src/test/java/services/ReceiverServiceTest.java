package services;

import org.junit.Test;
import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import javax.jms.ExceptionListener;

/**
 * Created with IntelliJ IDEA.
 * User: ddavis
 * Date: 7/29/12
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReceiverServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(
        ReceiverServiceTest.class);

    //SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();

    @Test
    public void basicTest() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        ReceiverService service = new ReceiverService(container);
        assertNotNull(service);
    }
}
