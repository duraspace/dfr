/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.domain;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public enum SyncProcessState {
    STOPPED, // the sync process is not running
    STARTING,
    RUNNING, // the sync process is running - ie it is monitoring directories and trying to upload
    STOPPING,
    ERROR // the system is not running.
}