package org.duraspace.dfr.sync.service;

import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.duraspace.dfr.sync.domain.SyncProcessStats;
import org.duraspace.dfr.test.AbstractTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SyncProcessManagerImplTest extends AbstractTest {
    private SyncProcessManagerImpl syncProcessManagerImpl;
    private SyncConfigurationManager syncConfigurationManager;

    private class TestSyncStateChangeListener
        implements SyncStateChangeListener {
        private SyncProcessState state = null;

        @Override
        public void stateChanged(SyncStateChangedEvent event) {
            state = event.getProcessState();
        }

        public SyncProcessState getState() {
            return this.state;
        }

    };

    private TestSyncStateChangeListener listener;

    @Before
    public void setUp() throws Exception {
        super.setup();
        syncConfigurationManager = createMock(SyncConfigurationManager.class);
        this.syncProcessManagerImpl =
            new SyncProcessManagerImpl(syncConfigurationManager);
        
        listener =
            new TestSyncStateChangeListener();
        this.syncProcessManagerImpl.addSyncStateChangeListener(listener);
    }

    @Test
    public void testStart() {
        replay();
        syncProcessManagerImpl.start();
        Assert.assertEquals(SyncProcessState.RUNNING, this.listener.getState());
       
    }

    @Test
    public void testStop() {
        replay();
        syncProcessManagerImpl.start();
        syncProcessManagerImpl.stop();
        Assert.assertEquals(SyncProcessState.STOPPED, this.listener.getState());
    }

    @Test
    public void testCleanStart() {
        replay();
        syncProcessManagerImpl.cleanStart();
        Assert.assertEquals(SyncProcessState.RUNNING, this.listener.getState());
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

}
