/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.duracloud.sync.endpoint.DuraStoreChunkSyncEndpoint;
import org.duracloud.sync.endpoint.MonitoredFile;
import org.duracloud.sync.endpoint.SyncEndpoint;
import org.duracloud.sync.mgmt.ChangedList;
import org.duracloud.sync.mgmt.StatusManager;
import org.duracloud.sync.mgmt.SyncManager;
import org.duracloud.sync.mgmt.SyncSummary;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * The SyncProcessManagerImpl is an implementation of the SyncProcessManager
 * interface. It coordinates the various elements from synctool that perform the
 * synchronization activites.
 * 
 * @author Daniel Bernstein
 */
@Component
public class SyncProcessManagerImpl implements SyncProcessManager {
    private static final int CHANGE_LIST_MONITOR_FREQUENCY = 5000;
    private static Logger log =
        LoggerFactory.getLogger(SyncProcessManagerImpl.class);
    private InternalState currentState;
    private SyncConfigurationManager syncConfigurationManager;
    private StoppedState stoppedState = new StoppedState();
    private StartingState startingState = new StartingState();
    private RunningState runningState = new RunningState();
    private StoppingState stoppingState = new StoppingState();
    private PausingState pausingState = new PausingState();
    private PausedState pausedState = new PausedState();
    private ResumingState resumingState = new ResumingState();
    
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
    public void resume() throws SyncProcessException {
        this.currentState.resume();
    }

    @Override
    public void stop() {
        this.currentState.stop();
    }

    @Override
    public void pause() {
        this.currentState.pause();
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
        this.syncStartedDate = new Date();
        setError(null);
        startAsynchronously();
    }
    
    private void startAsynchronously() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    startSyncProcess();
                    changeState(runningState);
                } catch (SyncProcessException e) {
                    log.error("start failed: " + e.getMessage(), e);
                }
            }
        }).start();
    }

    private void resumeImpl() throws SyncProcessException {
        changeState(resumingState);
        startAsynchronously();
    }
    
    private void startSyncProcess() throws SyncProcessException {
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
                                          CHANGE_LIST_MONITOR_FREQUENCY); // change list poll frequency
            syncManager.beginSync();

            dirWalker = DirWalker.start(dirs, null);
            dirMonitor =
                new DirectoryUpdateMonitor(dirs,
                                           CHANGE_LIST_MONITOR_FREQUENCY,
                                           false);
            dirMonitor.startMonitor();
        } catch (ContentStoreException e) {
            String message = "unable to get primary content store.";
            handleStartupException(message, e);
        } catch (Exception e){
            String message = "Unexpected error: " + e.getMessage();
            handleStartupException(message, e);
        }
    }

    private void handleStartupException(String message, Exception e)
        throws SyncProcessException {
        log.error(message, e);
        setError(new SyncProcessError(e.getMessage()));
        shutdownSyncProcess();
        changeState(stoppingState);
        changeState(stoppedState);
        throw new SyncProcessException(message, e);
    }

    private void setError(SyncProcessError error) {
        this.error = error;
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
        if(this.syncManager != null){
            this.syncManager.terminateSync();
        }
        try{
            this.dirMonitor.stopMonitor();
        }catch(Exception ex){
            log.warn("stop monitor failed: " + ex.getMessage());
        }
        
        if(this.dirWalker != null){
            this.dirWalker.stopWalk();
        }
    }
    
    private void resetChangeList() {
        ChangedList.getInstance().clear();
    }
    
    @SuppressWarnings("unused")
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

        private void waitForStateChange() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopImpl()  {
        changeState(stoppingState);
        
        final Thread t = new Thread() {
            @Override
            public void run() {
                shutdownSyncProcess();
                syncStartedDate = null;
                
                SyncManager sm = SyncProcessManagerImpl.this.syncManager;
                while (!sm.getFilesInTransfer().isEmpty()) {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                syncConfigurationManager.purgeWorkDirectory();
                resetChangeList();

                changeState(stoppedState);
            }
        };

        t.start();
    }

    private void pauseImpl() {
        changeState(pausingState);
        final Thread t = new Thread() {
            @Override
            public void run() {
                shutdownSyncProcess();
                SyncManager sm = SyncProcessManagerImpl.this.syncManager;
                while (!sm.getFilesInTransfer().isEmpty()) {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                changeState(pausedState);
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
        public void resume() throws SyncProcessException{
            // do nothing by default
        }

        @Override
        public void pause() {
            //do nothing by default
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

    
    private class PausedState extends InternalState {
        @Override
        public void resume() throws SyncProcessException {
            resumeImpl();
        }

        @Override
        public void stop() {
            stopImpl();
        }

        @Override
        public SyncProcessState getProcessState() {
            return SyncProcessState.PAUSED;
        }
    }

    
    private class StartingState extends InternalState {
        @Override
        public SyncProcessState getProcessState() {
            return SyncProcessState.STARTING;
        }
    }

    private class ResumingState extends InternalState {
        @Override
        public SyncProcessState getProcessState() {
            return SyncProcessState.RESUMING;
        }
    }

    private class RunningState extends InternalState {

        @Override
        public void stop() {
            stopImpl();
        }
        
        @Override
        public void pause() {
            pauseImpl();
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

    private class PausingState extends InternalState {
        @Override
        public SyncProcessState getProcessState() {
            return SyncProcessState.PAUSING;
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
    public List<SyncSummary> getFailures() {
        if (this.syncManager != null) {
            return StatusManager.getInstance().getFailed();
        }
        return new LinkedList<SyncSummary>();
    }

    @Override
    public List<SyncSummary> getRecentlyCompleted() {
        if (this.syncManager != null) {
            return StatusManager.getInstance().getRecentlyCompleted();
        }
        return new LinkedList<SyncSummary>();
    }


    @Override
    public List<File> getQueuedFiles() {
        return ChangedList.getInstance().peek(10);
    }
}
