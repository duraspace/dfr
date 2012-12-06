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


    public FedoraObjectStoreEvent ingest(FedoraObjectStoreEvent event) {
        logger.debug("Processing Fedora Object Event - " + event.getEventID());
        FedoraObject fdo = event.getFedoraObject();
        fedoraObjectStore.ingest(fdo, "Ingested via DfR");
        return event;
    }

    public FedoraObjectStoreEvent purge(FedoraObjectStoreEvent event) throws OCSException {
        logger.debug("Processing Fedora Object Event - " + event.getEventID());
        Map<String, String> metadata = event.getMetadata();
        String pid = metadata.get("PID");
        if (pid != null) {
          fedoraObjectStore.purge(pid, "Purged via DfR");
        }
        return event;
    }

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
