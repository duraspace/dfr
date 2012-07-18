/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.duracloud.sync.endpoint.MonitoredFile;
import org.duraspace.dfr.sync.domain.DirectoryConfig;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.duraspace.dfr.sync.domain.SyncProcessStats;
import org.duraspace.dfr.test.AbstractTest;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SyncProcessManagerImplTest extends AbstractTest {
    private SyncProcessManagerImpl syncProcessManagerImpl;
    private SyncConfigurationManager syncConfigurationManager;
    private ContentStore contentStore;
    private ContentStoreManagerFactory contentStoreManagerFactory;

    class TestSyncStateListener implements SyncStateChangeListener {
        private CountDownLatch latch = new CountDownLatch(1);
        private SyncProcessState state;

        public TestSyncStateListener(SyncProcessState state) {
            this.state = state;
        }

        @Override
        public void stateChanged(SyncStateChangedEvent event) {
            if (event.getProcessState() == this.state) {
                latch.countDown();
            }
        }

        public boolean success() {
            try {
                return latch.await(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        super.setup();
        syncConfigurationManager = createMock(SyncConfigurationManager.class);
        this.contentStore = createMock(ContentStore.class);
        this.contentStoreManagerFactory =
            createMock(ContentStoreManagerFactory.class);
        this.syncProcessManagerImpl =
            new SyncProcessManagerImpl(syncConfigurationManager,
                                       this.contentStoreManagerFactory);

    }

    protected void setupStart() throws ContentStoreException {
        
        File directory = new File(System.getProperty("java.io.tmpdir") + File.separator + "test-" + System.currentTimeMillis());
        directory.mkdirs();
        directory.deleteOnExit();
        
        ContentStoreManager contentStoreManager =
            createMock(ContentStoreManager.class);
        contentStoreManager.login(EasyMock.isA(Credential.class));

        EasyMock.expectLastCall();

        EasyMock.expect(contentStoreManagerFactory.create())
                .andReturn(contentStoreManager);

        EasyMock.expect(contentStoreManager.getPrimaryContentStore())
                .andReturn(contentStore);

        EasyMock.expect(this.contentStore.getSpaceContents(EasyMock.isA(String.class)))
                .andAnswer(new IAnswer<Iterator<String>>() {
                    @Override
                    public Iterator<String> answer() throws Throwable {
                        return Arrays.asList(new String[] {
                            "testFile1", "testFile2" }).iterator();
                    }
                });

        DirectoryConfigs dconfigs = new DirectoryConfigs();
        dconfigs.add(new DirectoryConfig(directory.getAbsolutePath()));
        EasyMock.expect(this.syncConfigurationManager.retrieveDirectoryConfigs())
                .andReturn(dconfigs);

        DuracloudConfiguration dc = createMock(DuracloudConfiguration.class);
        EasyMock.expect(dc.getUsername()).andReturn("testusername");
        EasyMock.expect(dc.getPassword()).andReturn("testpassword");
        EasyMock.expect(dc.getSpaceId()).andReturn("testspace");

        EasyMock.expect(this.syncConfigurationManager.retrieveDuracloudConfiguration())
                .andReturn(dc);
    }

    @Test
    public void testStop() throws SyncProcessException, ContentStoreException {
        setupStart();
        replay();

        TestSyncStateListener listener =
            new TestSyncStateListener(SyncProcessState.STOPPED);
        syncProcessManagerImpl.addSyncStateChangeListener(listener);
        syncProcessManagerImpl.start();
        syncProcessManagerImpl.stop();
        Assert.assertTrue(listener.success());
    }

    @Test
    public void testStart()
        throws SyncProcessException,
            ContentStoreException {
        setupStart();
        replay();
        TestSyncStateListener listener =
            new TestSyncStateListener(SyncProcessState.RUNNING);
        syncProcessManagerImpl.addSyncStateChangeListener(listener);
        syncProcessManagerImpl.start();
        Assert.assertTrue(listener.success());
    }

    @Test
    public void testCleanStart()
        throws SyncProcessException,
            ContentStoreException {
        setupStart();
        replay();
        TestSyncStateListener listener =
            new TestSyncStateListener(SyncProcessState.RUNNING);
        syncProcessManagerImpl.addSyncStateChangeListener(listener);
        syncProcessManagerImpl.cleanStart();
        Assert.assertTrue(listener.success());
    }

    @Test
    public void testGetProcessState() {
        replay();
        SyncProcessState state = syncProcessManagerImpl.getProcessState();
        Assert.assertEquals(SyncProcessState.STOPPED, state);
    }

    @Test
    public void testGetProcessStats() {
        replay();
        SyncProcessStats stats = syncProcessManagerImpl.getProcessStats();
        Assert.assertNotNull(stats);
    }

    @Test
    public void testGetMonitoredFiles() {
        replay();
        List<MonitoredFile> files = syncProcessManagerImpl.getMonitoredFiles();
        Assert.assertNotNull(files);
    }

    @Test
    public void testGetQueuedFiles() {
        replay();
        List<File> files = syncProcessManagerImpl.getQueuedFiles();
        Assert.assertNotNull(files);
    }

}
