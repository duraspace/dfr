package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates OCS events resulting in Fedora operation into messages to the
 * Fedora repository..
 */
public class FedoraEventHandler {

    private static final Logger logger =
        LoggerFactory.getLogger(FedoraEventHandler.class);

    public FedoraObject ingest(FedoraObjectEvent event) {
        logger.debug("Processing Fedora Object Event - " + event.getEventID());
        return event.getFedoraObject();
    }

}
