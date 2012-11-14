package org.duraspace.dfr.ocs.fedora;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import org.duraspace.dfr.ocs.core.OCSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event related to a Fedora object.
 */
public class FedoraObjectEvent implements OCSEvent {

    private static final Logger logger =
        LoggerFactory.getLogger(FedoraObjectEvent.class);

    /** Identifies the event to some level of uniqueness. */
    private final String eventID;

    /** Events related to this event. */
    private final List<OCSEvent> relatedEvents;

    /** Metadata about the event. */
    private final Map<String, String> metadata;

    /** The Fedora object that is the subject of this event. */
    private final FedoraObject fedoraObject;

    /**
     * Creates an instance.
     *
     * @param eventID the ID of this event to some level of uniqueness
     * @param fdo the <code>FedoraObject</code> that is the event's subject
     */
    public FedoraObjectEvent(String eventID, FedoraObject fdo) {

        // Check required arguments are set.
        if (eventID == null || fdo == null) {
            throw new NullPointerException();
        }

        logger.debug("Creating a FedoraObjectEvent - " + eventID);

        this.eventID = eventID;
        this.fedoraObject = fdo;
        this.metadata = new HashMap<String, String>();
        this.relatedEvents = new ArrayList<OCSEvent>();

    }

    @Override
    public String getEventID() {
        return eventID;
    }

    @Override
    public List<OCSEvent> getRelatedEvents() {
        return relatedEvents;
    }

    /**
     * Gets the metadata about the event.
     *
     * @return the metadata, never <code>null</code>.
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * The Fedora object that is the subject of this event.
     *
     * @return a <code>FedoraObject</code>.
     */
    public FedoraObject getFedoraObject() {
        return fedoraObject;
    }

}
