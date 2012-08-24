/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import org.duraspace.dfr.sync.domain.SyncProcessState;

/**
 * This class encapsulates the state change rules for the SyncProcessManager.
 * 
 * @author Daniel Bernstein
 * 
 */
public class SyncProcessStateTransitionValidator {
    /**
     * 
     * @param currentState
     * @param newState
     * @return true if the state transition is valid
     */
    public boolean validate(SyncProcessState from, SyncProcessState to) {
        if (from == SyncProcessState.STOPPED) {
            if (to == SyncProcessState.STARTING) {
                return true;
            }
        } else if (from == SyncProcessState.STARTING) {
            if (to == SyncProcessState.RUNNING || to == SyncProcessState.ERROR) {
                return true;
            }
        } else if (from == SyncProcessState.RUNNING) {
            if (to == SyncProcessState.STOPPING || to == SyncProcessState.PAUSING ||  to == SyncProcessState.ERROR) {
                return true;
            }
        } else if (from == SyncProcessState.STOPPING) {
            if (to == SyncProcessState.STOPPED || to == SyncProcessState.ERROR) {
                return true;
            }
        } else if (from == SyncProcessState.PAUSING) {
            if (to == SyncProcessState.PAUSED || to == SyncProcessState.ERROR) {
                return true;
            }
        } else if (from == SyncProcessState.PAUSED) {
            if (to == SyncProcessState.RESUMING || to == SyncProcessState.ERROR || 
                    to == SyncProcessState.STOPPING) {
                return true;
            }
        } else if (from == SyncProcessState.RESUMING) {
            if (to == SyncProcessState.RUNNING || to == SyncProcessState.ERROR) {
                return true;
            }
        }

        return false;
    }
}
