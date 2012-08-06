package org.duraspace.dfr.app.services;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Autowired
    @Qualifier("connectionFactory")
    private ActiveMQConnectionFactory connectionFactory;
    //private ActiveMQConnectionFactory connectionFactory =
    //    new ActiveMQConnectionFactory("tcp://localhost:61616");

    // private static BrokerService broker;

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
    public void testLister() {
        if (connectionFactory != null) { System.out.println("Connection Factory"); }
        service.getContainer().start();
        System.out.println("Testing the Listener");
        //thread(new GoodMessageProducer(), false);
        /*
        thread(new HelloWorldProducer(), false);
        thread(new HelloWorldConsumer(), false);
        Thread.sleep(1000);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldProducer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldProducer(), false);
        Thread.sleep(1000);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldProducer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldProducer(), false);
        thread(new HelloWorldProducer(), false);
        Thread.sleep(1000);
        thread(new HelloWorldProducer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldProducer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldProducer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldProducer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldConsumer(), false);
        thread(new HelloWorldProducer(), false);
        */
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public class GoodMessageProducer implements Runnable {
        DfRMessageProducer producer = new DfRMessageProducer(connectionFactory);
        public void run() {
           producer.sendMessage();
        }
    }

}
