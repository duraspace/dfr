/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

/**
 * @author Daniel Bernstein
 */

public class SyncProcessException extends Exception {
    public SyncProcessException(String message, Throwable t) {
        super(message, t);
    }
}
