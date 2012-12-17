/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An event pertaining to a {@link StorageObject}.
 *
 * Note: It remains to be determined if this duplicates the capabilities found
 *       in every execution environment or needs to be a POJO to avoid depending
 *       on them.  Also, the data model includes the notion of events for use
 *       in provenance, and, while this is useful, it may just be part of a
 *       larger auditing mechanism. Also, there is no interface and likely
 *       should be one with associated impls. DWD.
 */
public class StorageObjectEvent implements OCSEvent {

    private static final Logger logger = LoggerFactory.getLogger(
        StorageObjectEvent.class);

    /**
     * Types of events.
     */
    // Note: In process of being deprecated. Maybe. DWD
    public enum EventType {

        /** The storage object has been created. */
        CREATED,

        /** The storage object has been deleted. */
        DELETED

    }

    /** Identifies the event to some level of uniqueness. */
    private final String eventID;

    /** Identifies the type of the event. */
    private final EventType eventType;

    /** The storage object that is the subject of this event. */
    private final StorageObject storageObject;

    /** Metadata about the event. */
    // Note: The needs to be extended into a more flexible message metadata
    //       mechanism but good enough for now. DWD.
    private final Map<String, String> metadata;

    /** Events related to this event. */
    private final List<OCSEvent> relatedEvents;

    /**
     * Creates an instance.
     *
     * @param id            the id of the event, never <code>null</code>.
     * @param type          the type of event, never <code>null</code>.
     * @param storageObject the storageObject, never <code>null</code>.
     * @throws NullPointerException if type or storageObject is
     *                              <code>null</code>.
     */
    public StorageObjectEvent(String id, EventType type, StorageObject storageObject) {

        // Check required arguments are set.
        if (id == null || type == null || storageObject == null) {
            throw new NullPointerException();
        }

        this.eventID = id;
        this.eventType = type;
        this.storageObject = storageObject;
        this.metadata = new HashMap<>();
        this.relatedEvents = new ArrayList<>();

        logger.debug("Created {} event for storage object '{}'",
            type.toString(), storageObject.getId());

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
     * Gets the type of event.
     *
     * @return the type of event, never <code>null</code>.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the metadata about the event.
     * <p/>
     *
     * @return the metadata, never <code>null</code>.
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Gets the storage object to which the event applies.
     *
     * @return the storageObject, never <code>null</code>.
     * @throws OCSException if an IO or remote error occurs.
     */
    public StorageObject getStorageObject() throws OCSException {
        return storageObject;
    }

}
