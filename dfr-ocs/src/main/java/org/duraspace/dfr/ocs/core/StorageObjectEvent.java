package org.duraspace.dfr.ocs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An event pertaining to a {@link StorageObject}.
 *
 * Note: It remains to be determined if this duplicates the capabilities found
 *       in every execution environment or needs to be a POJO to avoid depending
 *       on them.  Also, the data model includes the notion of events for use
 *       in provenance, and, while this is useful, it may just be part of a
 *       larger auditing mechanism.  DWD.
 */
public class StorageObjectEvent {

    private static final Logger logger = LoggerFactory.getLogger(
        StorageObjectEvent.class);

    /** Types of events. */
    public enum Type {
        /** The storage object has been created. */
        CREATED,

        /** The storage object has been deleted. */
        DELETED
    }

    /** Identifies the event to some level of uniqueness. */
    private final String id;

    /** Identifies the type of the event. */
    // Note: The needs to be extended into a more flexible message metadata
    //       mechanism.
    private final Type type;
    private final StorageObject storageObject;

    /**
     * Creates an instance.
     *
     * @param id the id of the event, never <code>null</code>.
     * @param type the type of event, never <code>null</code>.
     * @param storageObject the storageObject, never <code>null</code>.
     * @throws NullPointerException if type or storageObject is
     *         <code>null</code>.
     */
    public StorageObjectEvent(String id, Type type, StorageObject storageObject) {
        if (id == null || type == null || storageObject == null) {
            throw new NullPointerException();
        }
        this.id = id;
        this.type = type;
        this.storageObject = storageObject;
        logger.debug("Created {} event for storage object '{}'",
                type.toString(), storageObject.getId());
    }

    /**
     * Gets the id of the event.
     * 
     * @return the id of the event, never <code>null</code>.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the type of event.
     *
     * @return the type of event, never <code>null</code>.
     */
    public Type getType() {
        return type;
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
