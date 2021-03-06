/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import org.duraspace.dfr.ocs.core.OCSException;

/**
 * A place where Fedora objects can be stored.
 */
public interface FedoraObjectStore {
    /**
     * Ingests a new object.
     *
     * @param object the object to ingest.
     * @param logMessage the log message to use.
     * @return <code>true</code> if the object was successfully ingested,
     *         <code>false</code> if the object was not ingested because
     *         one with the same pid already exists.
     * @throws org.duraspace.dfr.ocs.core.OCSException if an IO or remote error occurs.
     */
    boolean ingest(FedoraObject object, String logMessage) throws OCSException;

    /**
     * Exports an existing object.
     *
     * @param pid the pid of the object to export.
     * @return the object, or <code>null</code> if it doesn't exist.
     * @throws OCSException if an IO or remote error occurs.
     */
    FedoraObject export(String pid) throws OCSException;

    /**
     * Purges an existing object. If the object doesn't exist, this is a no-op.
     *
     * @param pid the pid of the object to purge.
     * @param logMessage the log message to use.
     * @throws OCSException if an IO or remote error occurs.
     */
    void purge(String pid, String logMessage) throws OCSException;

    //void initialize();
}
