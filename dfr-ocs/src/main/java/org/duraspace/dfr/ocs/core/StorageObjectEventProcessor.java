package org.duraspace.dfr.ocs.core;

/**
 * Processes {@link StorageObjectEvent}s and sends them to a
 * {@link FedoraObjectStore}.
 */
public interface StorageObjectEventProcessor {
    /**
     * Sets the Fedora object store to which the results should be sent.
     *
     * @param fedoraObjectStore the Fedora object store, never <code>null</code>.
     */
    void setFedoraObjectStore(FedoraObjectStore fedoraObjectStore);

    /**
     * Processes a single event.
     *
     * @param event the event, never <code>null</code>.
     * @throws OCSException if an IO or remote error occurs.
     */
    void process(StorageObjectEvent event) throws OCSException;
}
