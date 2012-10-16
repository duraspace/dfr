package org.duraspace.dfr.ocs.it;

//import com.github.cwilper.fcrepo.dto.core.FedoraObject;
//import com.yourmediashelf.fedora.client.FedoraClient;
//import com.yourmediashelf.fedora.client.FedoraCredentials;
//import org.apache.commons.codec.digest.DigestUtils;
//import org.duraspace.dfr.ocs.duracloud.DuraCloudMessagingClient;
//import org.duraspace.dfr.ocs.duracloud.DuraCloudStorageListener;
//import org.duraspace.dfr.ocs.duracloud.DuraCloudTempSpace;
//import org.duraspace.dfr.ocs.fedora.FedoraRepository;
//import org.duraspace.dfr.ocs.simpleproc.SimpleProcessor;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;

/**
 * Integration test covering the end-to-end OCS data flow between a running
 * DuraCloud instance and a running Fedora instance.
 */
public class DuraCloudFedoraIT {

    // This will be revised to use the Camel testing framework since it
    // does the end-to-end routing.

//    private static final String CONTENT_ID = "foo";
//    private static final String CONTENT = "bar";
//
//    private static DuraCloudTempSpace tempSpace;
//    private static DuraCloudStorageListener listener;
//    private static DuraCloudMessagingClient messagingClient;
//    private static FedoraRepository fedoraRepository;
//
//    @BeforeClass
//    public static void setUp() throws Exception {
//        FedoraCredentials credentials = new FedoraCredentials(
//                ITConstants.FEDORA_BASE_URL,
//                ITConstants.FEDORA_USERNAME,
//                ITConstants.FEDORA_PASSWORD);
//        FedoraClient client = new FedoraClient(credentials);
//        fedoraRepository = new FedoraRepository(client);
//        tempSpace = new DuraCloudTempSpace();
//        SimpleProcessor processor = new SimpleProcessor(
//                ITConstants.DURACLOUD_HOSTNAME + ":",
//                ITConstants.DURASTORE_BASE_URL);
//        processor.setFedoraObjectStore(fedoraRepository);
//        listener = new DuraCloudStorageListener(tempSpace.getContentStore());
//        listener.setProcessor(processor);
//        messagingClient = new DuraCloudMessagingClient(listener);
//    }
//
//    @Test
//    public void addThenDelete() throws Exception {
//        // add via DuraCloud and wait for Fedora object to show up
//        tempSpace.addContent(CONTENT_ID, CONTENT);
//        String pid = getPid(CONTENT_ID);
//        FedoraObject fedoraObject = waitForFedoraObject(pid);
//
//        // delete via DuraCloud and wait for Fedora object to get purged
//        tempSpace.deleteContent(CONTENT_ID);
//        waitForFedoraObjectPurge(pid);
//    }
//
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
//    private String getPid(String contentId) {
//        String url = ITConstants.DURASTORE_BASE_URL + "/" + tempSpace.getId()
//                + "/" + contentId + "?storeID="
//                + tempSpace.getContentStore().getStoreId();
//        return ITConstants.DURACLOUD_HOSTNAME + ":" + DigestUtils.md5Hex(url);
//    }
//
//    private FedoraObject waitForFedoraObject(String pid) throws Exception {
//        long msCount = 0;
//        FedoraObject fedoraObject = null;
//        while (fedoraObject == null && msCount < 3000) {
//            fedoraObject = fedoraRepository.export(pid);
//            msCount += 50;
//            try { Thread.sleep(50); } catch (InterruptedException e) { }
//        }
//        if (msCount == 3000) {
//            Assert.fail("Waited > 3 seconds and never saw " + pid +
//                    " ingested into Fedora repository");
//        }
//        return fedoraObject;
//    }
//
//    private FedoraObject waitForFedoraObjectPurge(String pid) throws Exception {
//        long msCount = 0;
//        FedoraObject fedoraObject = fedoraRepository.export(pid);
//        while (fedoraObject != null && msCount < 3000) {
//            msCount += 50;
//            try { Thread.sleep(50); } catch (InterruptedException e) { }
//            fedoraObject = fedoraRepository.export(pid);
//        }
//        if (msCount == 3000) {
//            Assert.fail("Waited > 3 seconds and never saw " + pid +
//                    " purged from Fedora repository");
//        }
//        return fedoraObject;
//    }
//
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
//
//    @AfterClass
//    public static void tearDown() {
//        messagingClient.close();
//        listener.close();
//        tempSpace.close();
//    }

}
