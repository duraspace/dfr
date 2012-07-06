/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.util.ArrayList;
import java.util.List;

import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.duraspace.dfr.sync.domain.SyncProcessStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The SyncProcessManagerImpl is an implementation of the SyncProcessManager
 * interface.
 * 
 * @author Daniel Bernstein
 */
@Component
public class SyncProcessManagerImpl implements SyncProcessManager {

    private InternalState currentState;
    private SyncConfigurationManager syncConfigurationManager;
    private StoppedState stoppedState = new StoppedState();
    private StartingState startingState = new StartingState();
    private RunningState runningState = new RunningState();
    private StoppingState stoppingState = new StoppingState();
    private List<SyncStateChangeListener> listeners;
    private SyncProcessStateTransitionValidator syncProcessStateTransitionValidator;
    @Autowired
    public SyncProcessManagerImpl(
        SyncConfigurationManager syncConfigurationManager) {
        this.syncConfigurationManager = syncConfigurationManager;
        this.currentState = this.stoppedState;
        this.listeners = new ArrayList<SyncStateChangeListener>();
        this.syncProcessStateTransitionValidator = new SyncProcessStateTransitionValidator();
    }

    @Override
    public void start() {
        this.currentState.start();
    }

    @Override
    public void stop() {
        this.currentState.stop();
    }

    @Override
    public void cleanStart() {
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

    private void startImpl() {
        changeState(startingState);
        // perfrom start logic here
        changeState(runningState);
    }

    private void cleanStartImpl() {
        if (this.currentState.getProcessState()
                             .equals(SyncProcessState.RUNNING)) {
            stopImpl();
        }

        // wait for complete stop
        //set clean 
        startImpl();
    }


    private void stopImpl() {
        changeState(stoppingState);
        // TODO perform stop logic here
        changeState(stoppedState);

    }

    // internally the SyncProcessManagerImpl makes use of the Gof4 State
    // pattern.
    private abstract class InternalState implements SyncProcess {
        @Override
        public void start() {
            //do nothing by default
        }

        @Override
        public void stop() {
            //do nothing by default
        }

        @Override
        public void cleanStart() {
            cleanStartImpl();
        }

        @Override
        public SyncProcessStats getProcessStats() {
            // TODO Auto-generated method stub
            return new SyncProcessStats();
        }
    }

    private class StoppedState extends InternalState {
        @Override
        public void start() {
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
}
