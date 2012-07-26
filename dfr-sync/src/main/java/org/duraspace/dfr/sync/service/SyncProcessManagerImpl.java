/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.duracloud.sync.endpoint.DuraStoreChunkSyncEndpoint;
import org.duracloud.sync.endpoint.MonitoredFile;
import org.duracloud.sync.endpoint.SyncEndpoint;
import org.duracloud.sync.mgmt.ChangedList;
import org.duracloud.sync.mgmt.SyncManager;
import org.duracloud.sync.monitor.DirectoryUpdateMonitor;
import org.duracloud.sync.walker.DirWalker;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.duraspace.dfr.sync.domain.SyncProcessStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The SyncProcessManagerImpl is an implementation of the SyncProcessManager
 * interface. It coordinates the various elements from synctool that perform the
 * synchronization activites.
 * 
 * @author Daniel Bernstein
 */
@Component
public class SyncProcessManagerImpl implements SyncProcessManager {
    private static Logger log =
        LoggerFactory.getLogger(SyncProcessManagerImpl.class);
    private InternalState currentState;
    private SyncConfigurationManager syncConfigurationManager;
    private StoppedState stoppedState = new StoppedState();
    private StartingState startingState = new StartingState();
    private RunningState runningState = new RunningState();
    private StoppingState stoppingState = new StoppingState();
    private List<SyncStateChangeListener> listeners;
    private SyncProcessStateTransitionValidator syncProcessStateTransitionValidator;

    private SyncManager syncManager;
    private DirWalker dirWalker;
    private DirectoryUpdateMonitor dirMonitor;

    private SyncProcessError error;

    private ContentStoreManagerFactory contentStoreManagerFactory;

    private Date syncStartedDate = null;

    @Autowired
    public SyncProcessManagerImpl(
        SyncConfigurationManager syncConfigurationManager,
        ContentStoreManagerFactory contentStoreManagerFactory) {
        this.syncConfigurationManager = syncConfigurationManager;
        this.currentState = this.stoppedState;
        this.listeners = new ArrayList<SyncStateChangeListener>();
        this.syncProcessStateTransitionValidator =
            new SyncProcessStateTransitionValidator();

        this.contentStoreManagerFactory = contentStoreManagerFactory;
    }

    @Override
    public SyncProcessError getError() {
        return this.error;
    }

    @Override
    public void clearError() {
        this.error = null;
    }

    @Override
    public void start() throws SyncProcessException {
        this.currentState.start();
    }

    @Override
    public void stop() {
        this.currentState.stop();
    }

    @Override
    public void cleanStart() throws SyncProcessException {
        this.currentState.cleanStart();
    }

    @Override
    public SyncProcessState getProcessState() {
        return this.currentState.getProcessState();
    }

    @Override
    public SyncProcessStats getProcessStats() {
        return this.currentState.getProcessStats();
    }

    @Override
    public void
        addSyncStateChangeListener(SyncStateChangeListener syncStateChangeListener) {
        this.listeners.add(syncStateChangeListener);
    }

    @Override
    public void
        removeSyncStateChangeListener(SyncStateChangeListener syncStateChangeListener) {
        this.listeners.remove(syncStateChangeListener);
    }

    private void fireStateChanged(SyncProcessState state) {
        SyncStateChangedEvent event = new SyncStateChangedEvent(state);
        for (SyncStateChangeListener listener : listeners) {
            listener.stateChanged(event);
        }
    }

    private synchronized void changeState(InternalState state) {
        SyncProcessState current = this.currentState.getProcessState();
        SyncProcessState incoming = state.getProcessState();
        boolean validStateChange =
            syncProcessStateTransitionValidator.validate(current, incoming);
        if (validStateChange) {
            this.currentState = state;
            fireStateChanged(this.currentState.getProcessState());
        }
    }

    private void startImpl() throws SyncProcessException {
        changeState(startingState);
        DuracloudConfiguration dc =
            this.syncConfigurationManager.retrieveDuracloudConfiguration();

        try {
            ContentStoreManager csm = contentStoreManagerFactory.create();
            String username = dc.getUsername();
            csm.login(new Credential(username, dc.getPassword()));
            ContentStore contentStore = csm.getPrimaryContentStore();
            SyncEndpoint syncEndpoint =
                new DuraStoreChunkSyncEndpoint(contentStore,
                                               username,
                                               dc.getSpaceId(),
                                               false,
                                               1073741824); // 1GB chunk size
            DirectoryConfigs directories =
                this.syncConfigurationManager.retrieveDirectoryConfigs();
            List<File> dirs = directories.toFileList();
            syncManager = new SyncManager(dirs, syncEndpoint, 3, // threads
                                          10000); // change list poll frequency
            syncManager.beginSync();

            dirWalker = DirWalker.start(dirs);
            dirMonitor = new DirectoryUpdateMonitor(dirs, 10000, false);
            dirMonitor.startMonitor();
            this.syncStartedDate = new Date();
        } catch (ContentStoreException e) {
            String message = "unable to get primary content store.";
            log.error(message, e);
            this.error = new SyncProcessError(e.getMessage());
            shutdownSyncProcess();
            changeState(stoppedState);
            throw new SyncProcessException(message, e);
        }
        // perfrom start logic here
        changeState(runningState);
    }

    private SyncProcessStats getProcessStatsImpl() {
        int queueSize = ChangedList.getInstance().getListSize();
        return new SyncProcessStats(this.syncStartedDate,
                                    null,
                                    0,
                                    0,
                                    0,
                                    queueSize);
    }

    private void shutdownSyncProcess() {
        this.syncManager.terminateSync();
        this.dirMonitor.stopMonitor();
        this.dirWalker.stopWalk();
        this.syncStartedDate = null;
    }

    private class InternalListener implements SyncStateChangeListener {
        private CountDownLatch latch = new CountDownLatch(1);
        private SyncProcessState state;

        public InternalListener(SyncProcessState state) {
            this.state = state;
        }

        @Override
        public void stateChanged(SyncStateChangedEvent event) {
            if (event.getProcessState() == this.state) {
                latch.countDown();
            }
        }

        public void waitForStateChange() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void cleanStartImpl() throws SyncProcessException {
        InternalListener listener =
            new InternalListener(SyncProcessState.STOPPED);
        addSyncStateChangeListener(listener);

        if (this.currentState.getProcessState()
                             .equals(SyncProcessState.RUNNING)) {
            stopImpl();
        }

        // wait for complete stop
        if (getProcessState() != SyncProcessState.STOPPED) {
            listener.waitForStateChange();
        }

        removeSyncStateChangeListener(listener);
        // set clean
        startImpl();
    }

    private void stopImpl() {
        changeState(stoppingState);
        shutdownSyncProcess();
        final Thread t = new Thread() {
            @Override
            public void run() {
                SyncManager sm = SyncProcessManagerImpl.this.syncManager;
                while (!sm.getFilesInTransfer().isEmpty()) {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                changeState(stoppedState);
            }
        };

        t.start();
    }

    // internally the SyncProcessManagerImpl makes use of the Gof4 State
    // pattern.
    private abstract class InternalState implements SyncProcess {
        @Override
        public void start() throws SyncProcessException {
            // do nothing by default
        }

        @Override
        public void stop() {
            // do nothing by default
        }

        @Override
        public void cleanStart() throws SyncProcessException {
            cleanStartImpl();
        }

        @Override
        public SyncProcessStats getProcessStats() {
            return getProcessStatsImpl();
        }
    }

    private class StoppedState extends InternalState {
        @Override
        public void start() throws SyncProcessException {
            startImpl();
        }

        @Override
        public SyncProcessState getProcessState() {
            return SyncProcessState.STOPPED;
        }
    }

    private class StartingState extends InternalState {
        @Override
        public SyncProcessState getProcessState() {
            return SyncProcessState.STARTING;
        }
    }

    private class RunningState extends InternalState {

        @Override
        public void stop() {
            stopImpl();
        }

        @Override
        public SyncProcessState getProcessState() {
            return SyncProcessState.RUNNING;
        }
    }

    private class StoppingState extends InternalState {
        @Override
        public SyncProcessState getProcessState() {
            return SyncProcessState.STOPPING;
        }
    }

    @Override
    public List<MonitoredFile> getMonitoredFiles() {
        if (this.syncManager != null) {
            return this.syncManager.getFilesInTransfer();
        }
        return new LinkedList<MonitoredFile>();
    }

    @Override
    public List<File> getQueuedFiles() {
        return ChangedList.getInstance().peek(10);
    }
}