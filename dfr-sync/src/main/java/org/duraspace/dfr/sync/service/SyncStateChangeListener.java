/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public interface SyncStateChangeListener {
    public void stateChanged(SyncStateChangedEvent event);
}
