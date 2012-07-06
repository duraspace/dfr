/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

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
}
