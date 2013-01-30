/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
