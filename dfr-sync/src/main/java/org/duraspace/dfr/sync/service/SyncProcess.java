/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.util.List;

import org.duracloud.sync.mgmt.SyncSummary;
import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.duraspace.dfr.sync.domain.SyncProcessStats;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public interface SyncProcess {

    /**
     * Starts the sync process. Invocations are ignored if the sync process is
     * already running.
     */
    public void start() throws SyncProcessException;

    /**
     * Resumes the sync process from the paused state. Invocations are ignored
     * if the sync process is already running.
     */
    public void resume() throws SyncProcessException;

    /**
     * Stops the sync process. Invocations are ignored if the sync process is
     * already stopped. All work state is deleted. On start, the sync will start
     * from scratch after this method has been invoked.
     */
    public void stop();

    /**
     * Pauses the sync process. Any any stored queue state information will be
     * preserved when the sync process is restarted. It can be invoked from any
     * state.
     */
    public void pause();

    /**
     * Returns an enum designating the runtime state of the sync process
     * 
     * @return
     */
    public SyncProcessState getProcessState();

    /**
     * Returns stats related the sync process
     * 
     * @return
     */
    public SyncProcessStats getProcessStats();


}
