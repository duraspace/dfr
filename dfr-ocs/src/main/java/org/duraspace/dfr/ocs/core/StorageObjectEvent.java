package org.duraspace.dfr.ocs.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An event pertaining to a {@link StorageObject}.
 */
public class StorageObjectEvent {
    /**
     * Types of events.
     */
    public enum Type {
        /** The storage object has been created. */
        CREATED,

        /** The storage object has been deleted. */
        DELETED
    }

    private static final Logger logger = LoggerFactory.getLogger(
            StorageObjectEvent.class);

    private final String id;
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
