/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.core;

/**
 * Signals an IO or remote error occurred during OCS execution.
 */
public class OCSException extends RuntimeException {
    /**
     * Creates an instance with a message.
     *
     * @param message the message.
     */
    public OCSException(String message) {
        super(message);
    }

    /**
     * Creates an instance with a message and a cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public OCSException(String message, Throwable cause) {
        super(message, cause);
    }
}
