package org.duraspace.dfr.sync.service;

import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.duraspace.dfr.sync.domain.SyncProcessStats;

public interface SyncProcess {

    /**
     * Starts the sync process. Invocations are ignored if the sync process is already running.
     */
    public void start();

    /**
     * Stops the sync process.  Invocations are ignored if the sync process is already stopped.
     */
    public void stop();
    
    /**
     * Starts the sync process without regard for any stored queue state information.  
     * It can be invoked from any state.
     */
    public void cleanStart();

    /**
     * Returns an enum designating the runtime state of the sync process
     * @return
     */
    public SyncProcessState getProcessState();

    /**
     * Returns stats related the sync process
     * @return
     */
    public SyncProcessStats getProcessStats();

    
}
