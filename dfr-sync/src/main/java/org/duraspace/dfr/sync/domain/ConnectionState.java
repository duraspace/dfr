/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.sync.domain;

/**
 * Describes the state of the internet connection of the dfr-sync.
 * @author Daniel Bernstein
 *
 */
public enum ConnectionState {
    ONLINE,
    OFFLINE;
}
