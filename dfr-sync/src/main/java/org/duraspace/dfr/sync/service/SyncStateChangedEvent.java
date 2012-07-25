/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.util.EventObject;

import org.duraspace.dfr.sync.domain.SyncProcessState;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class SyncStateChangedEvent extends EventObject {

    private static final long serialVersionUID = 1L;

    private SyncProcessState syncProcessState;
    public SyncStateChangedEvent(SyncProcessState source) {
        super(source);
        this.syncProcessState = source;
    }

    public SyncProcessState getProcessState(){
        return this.syncProcessState;
    }

}
