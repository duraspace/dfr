/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.io.File;
import java.util.List;

import org.duracloud.sync.endpoint.MonitoredFile;
import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.duraspace.dfr.sync.domain.SyncProcessStats;

/**
 * The SyncProcessManager interface delineates the set of functions available
 * for controlling and retrieving state information about the current runtime
 * state of the sync process 
 * @author Daniel Bernstein
 *
 */
public interface SyncProcessManager extends SyncProcess{
      
    /**
     * 
     * @param syncStateChangeListener
     */
    public void addSyncStateChangeListener(SyncStateChangeListener syncStateChangeListener);

    /**
     * 
     * @param syncStateChangeListener
     */
    public void removeSyncStateChangeListener(SyncStateChangeListener syncStateChangeListener);

    /**
     * Returns a list of actively transfering (uploading) files
     * @return
     */
    public List<MonitoredFile> getMonitoredFiles();

    /**
     * 
     * @return
     */
    public List<File> getQueuedFiles();

    /**
     * 
     * @return
     */
    public SyncProcessError getError();

    /**
     * 
     */
    void clearError();
}
