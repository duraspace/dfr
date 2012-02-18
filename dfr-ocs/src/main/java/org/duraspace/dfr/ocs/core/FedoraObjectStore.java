package org.duraspace.dfr.ocs.core;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;

/**
 * A place where Fedora objects can be stored.
 */
public interface FedoraObjectStore {
    /**
     * Ingests a new object.
     *
     * @param object the object to ingest.
     * @throws OCSException if an IO or remote error occurs.
     */
    void ingest(FedoraObject object) throws OCSException;

    /**
     * Purges an existing object.
     *
     * @param pid the pid of the object to purge.
     * @throws OCSException if an IO or remote error occurs.
     */
    void purge(String pid) throws OCSException;
}
