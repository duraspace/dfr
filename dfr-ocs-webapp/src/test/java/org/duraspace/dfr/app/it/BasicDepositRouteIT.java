/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.app.it;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.app.org.duraspace.dfr.app.util.TempSpace;
import org.duraspace.dfr.ocs.duracloud.DuraCloudObjectStoreClient;
import org.junit.*;

import org.duraspace.dfr.ocs.fedora.FedoraObjectStoreClient;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * Integration test covering the end-to-end basic route from DuraCloud to
 * Fedora using a canonical test message. This IT does not attempt to test
 * all possible messages or all possible routes. It does represent the
 * forward route from user upload through object creation and ingest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(listeners = {
    DependencyInjectionTestExecutionListener.class,
    BasicDepositRouteIT.class})
@ContextConfiguration("classpath:/applicationContext.xml")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BasicDepositRouteIT extends AbstractTestExecutionListener {

    private static final Logger logger =
        LoggerFactory.getLogger(BasicDepositRouteIT.class);

    private static final String CONTENT_ID = "foo";
    private static final String CONTENT = "bar";

    @Autowired
    @Qualifier("sourceStoreClient")
    private DuraCloudObjectStoreClient sourceStoreClient;

    @Autowired
    @Qualifier("camelContext")
    private SpringCamelContext camelContext;

    @Autowired
    @Qualifier("destinationStoreClient")
    private FedoraObjectStoreClient destinationStoreClient;

    @Value("${dfr.contentsource.url}")
    private String sourceStoreUrl;

    @Value("${dfr.contentstore.host}")
    private String sourceStoreHost;

    @Value("${dfr.object.prefix}")
    private String pidPrefix;

    private static String spaceId = "temp" + System.currentTimeMillis();

    @Test
    public void addThenDelete() throws Exception {

        // Notify when one message is done
        NotifyBuilder notify = new NotifyBuilder(camelContext).whenDone(1).create();

        // add via DuraCloud and wait for notification message to show up.
        addContent(CONTENT_ID, CONTENT);

        // true means the notifier condition matched (= 1 message is done)
        boolean matches = notify.matches(10, TimeUnit.SECONDS);
        Assert.assertTrue(matches);

        // Check that the Fedora object was made
        String pid = getPid(CONTENT_ID);
        FedoraObject fedoraObject = waitForFedoraObject(pid);
        Assert.assertNotNull(fedoraObject);

        // delete via DuraCloud and wait for Fedora object to get purged
        // Note: We are not supported the delete message in this route.
        //tempSpace.deleteContent(CONTENT_ID);
        waitForFedoraObjectPurge(pid);

    }

// Note: We are not supported the delete message in this route. When we do
//       tests like these can be implemented in that route. DWD
//    @Test
//    public void addAlreadyExistingInFedora() throws Exception {
//        // add to Fedora so it already exists
//        String pid = getPid(CONTENT_ID);
//        FedoraObject fedoraObject = new FedoraObject().pid(pid).label("label");
//        fedoraRepository.ingest(fedoraObject, "logMessage");
//
//        // add via DuraCloud and ensure we get the expected error
//        messagingClient.setLastError(null);
//        tempSpace.addContent(CONTENT_ID, CONTENT);
//        try {
//            Throwable th = waitForListenerError(true);
//            if (!th.getMessage().contains("Fedora object already exists:")) {
//                Assert.fail("Didn't get expected exception message; message "
//                        + "was: " + th.getMessage());
//            }
//        } finally {
//            // clean up
//            tempSpace.deleteContent(CONTENT_ID);
//            waitForFedoraObjectPurge(pid);
//        }
//    }
//
//    @Test
//    public void deleteNonExistingInFedora() throws Exception {
//        // add via DuraCloud and wait for Fedora object to show up
//        tempSpace.addContent(CONTENT_ID, CONTENT);
//        String pid = getPid(CONTENT_ID);
//        FedoraObject fedoraObject = waitForFedoraObject(pid);
//
//        // delete via Fedora
//        fedoraRepository.purge(pid, "logMessage");
//
//        // delete via DuraCloud and ensure we don't get an error
//        messagingClient.setLastError(null);
//        tempSpace.deleteContent(CONTENT_ID);
//        Assert.assertNull(waitForListenerError(false));
//    }
//

    private void addContent(String contentId, String body) {
        try {
            byte[] bytes = body.getBytes("UTF-8");
            InputStream in = new ByteArrayInputStream(bytes);
            sourceStoreClient.getContentStore().
                addContent(spaceId, contentId, in, bytes.length,
                    "text/plain", null, null);
        } catch (ContentStoreException e) {
            throw new RuntimeException("Unexpected error adding content", e);
        } catch (UnsupportedEncodingException wontHappen) {
            throw new RuntimeException(wontHappen);
        }
    }

    private String getPid(String contentId) {
        String url = sourceStoreUrl + "/" + spaceId
                + "/" + contentId + "?storeID="
                + sourceStoreClient.getContentStore().getStoreId();
        //System.out.println("Pid Source - " + url);
        return pidPrefix + DigestUtils.md5Hex(url);
    }

    private FedoraObject waitForFedoraObject(String pid) throws Exception {
        long msCount = 0;
        FedoraObject fedoraObject = null;
        while (fedoraObject == null && msCount < 10000) {
            fedoraObject = destinationStoreClient.export(pid);
            msCount += 50;
            try { Thread.sleep(50); } catch (InterruptedException e) { }
        }
        if (msCount == 10000) {
            Assert.fail("Waited > 10 seconds and never saw " + pid +
                    " ingested into Fedora repository");
        }
        return fedoraObject;
    }

    private FedoraObject waitForFedoraObjectPurge(String pid) throws Exception {
        long msCount = 0;
        FedoraObject fedoraObject = destinationStoreClient.export(pid);
        destinationStoreClient.purge(pid, "test");
        while (fedoraObject != null && msCount < 10000) {
            msCount += 50;
            try { Thread.sleep(50); } catch (InterruptedException e) { }
            fedoraObject = destinationStoreClient.export(pid);
        }
        if (msCount == 10000) {
            Assert.fail("Waited > 10 seconds and never saw " + pid +
                    " purged from Fedora repository");
        }
        return fedoraObject;
    }

//    private Throwable waitForListenerError(boolean failIfNot)
//            throws Exception {
//        long msCount = 0;
//        Throwable th = messagingClient.getLastError();
//        while (th == null && msCount < 3000) {
//            msCount += 50;
//            try { Thread.sleep(50); } catch (InterruptedException e) { }
//            th = messagingClient.getLastError();
//        }
//        if (msCount == 3000 && failIfNot) {
//            Assert.fail("Waited > 3 seconds and never saw an error from "
//                    + "the listener");
//        }
//        return th;
//    }

    @Override
    public void beforeTestClass(TestContext testContext) {
        sourceStoreClient = (DuraCloudObjectStoreClient)
            testContext.getApplicationContext().getBean("sourceStoreClient");
        testContext.
            setAttribute("tempSpace", new TempSpace(sourceStoreClient, spaceId));
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        TempSpace tempSpace = (TempSpace) testContext.getAttribute("tempSpace");
        tempSpace.close();
    }

//    /**
//     * Utility class for adding and removing a temp space for testing.
//     */
//    private class TempSpace {
//
//        private boolean closed = false;
//        private ContentStore store;
//
//        private TempSpace() {
//
//            logger.debug("Constructing a TempSpace");
//
//            boolean exists = false;
//            int count = 0;
//            store = sourceStoreClient.getContentStore();
//            while (!exists && count < 10) {
//                try {
//                    logger.info("Adding a temp space {}", spaceId);
//                    if (count == 0) {
//                        store.createSpace(spaceId, null);
//                    }
//                    store.getSpaceACLs(spaceId);
//                    exists = true;
//                } catch (NotFoundException e) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ie) {
//                        // NOOP
//                    }
//                    count++;
//                } catch (ContentStoreException e) {
//                    close();
//                    throw new RuntimeException(e);
//                }
//                if (count == 10) throw new RuntimeException("Temporary space " +
//                    spaceId + " never got created? Checked 10 times.");
//            }
//
//        }
//
//        private void close() {
//
//            if (!closed) {
//                try {
//                    logger.info("Deleting temp space {}", spaceId);
//                    store.deleteSpace(spaceId);
//                    sourceStoreClient.getContentStoreManager().logout();
//                    closed = true;
//                } catch (ContentStoreException e) {
//                    logger.error("Error closing", e);
//                }
//            }
//
//        }
//
//    }

}
