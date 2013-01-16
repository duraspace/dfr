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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Translates OCS events resulting in Fedora operation into messages to the
 * Fedora repository..
 */
public class FedoraEventHandler {

    private static final Logger logger =
        LoggerFactory.getLogger(FedoraEventHandler.class);

    /** A DFR processor used to make it easy to inter-operate with Fedora */
    private final FedoraObjectStore fedoraObjectStore;

    /**
     * Creates an instance.
     *
     * @param fedoraObjectStore the Fedora object store to use, never <code>null</code>.
     */
    public FedoraEventHandler(FedoraObjectStore fedoraObjectStore) {
        if (fedoraObjectStore == null) throw new NullPointerException();
        this.fedoraObjectStore = fedoraObjectStore;
        logger.debug("Constructing a Fedora Event Handler");
    }

    /**
     * Ingests a Fedora Object resulting from an event.
     *
     * @param event the ingest <code>Event</code>
     * @return the ingest <code>Event</code> with status
     */
    public FedoraObjectStoreEvent ingest(FedoraObjectStoreEvent event) {
        logger.debug("Processing Fedora Object Event - " + event.getEventID());
        FedoraObject fdo = event.getFedoraObject();
        String logMessage = event.getMetadata().get("log-message");
        if (logMessage == null) { logMessage = "Ingest via DfR"; }
        fedoraObjectStore.ingest(fdo, logMessage);
        return event;
    }

    /**
     * Purge a Fedora Object resulting from an event.
     *
     * @param event the purge <code>Event</code>
     * @return the purge <code>Event</code> with status
     * @throws OCSException is the purge failedJim
     */
    public FedoraObjectStoreEvent purge(FedoraObjectStoreEvent event) throws OCSException {
        logger.debug("Processing Fedora Object Event - " + event.getEventID());
        Map<String, String> metadata = event.getMetadata();
        String pid = metadata.get("PID");
        String logMessage = event.getMetadata().get("log-message");
        if (logMessage == null) { logMessage = "Purge via DfR"; }
        if (pid != null) {
          fedoraObjectStore.purge(pid, logMessage);
        }
        return event;
    }

    /**
     * Retrieves a copy of a Fedora object from the store.
     *
     * @param event the export <code>Event</code>
     * @return the export <code>Event</code> containing the Fedora Object
     * @throws OCSException if the export failed
     */
    public FedoraObjectStoreEvent export(FedoraObjectStoreEvent event) throws OCSException {
        logger.debug("Processing Fedora Object Event - " + event.getEventID());
        Map<String, String> metadata = event.getMetadata();
        String pid = metadata.get("PID");
        if (pid != null) {
            FedoraObject fdo = fedoraObjectStore.export(pid);
            event.setFedoraObject(fdo);
        }
        return event;
    }

}
