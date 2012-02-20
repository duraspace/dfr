package org.duraspace.dfr.ocs.core;

/**
 * A <code>StorageObjectEventProcessor</code> impl that makes the last received
 * event publicly available and settable, for testing.
 */
public class EventCapturingProcessor implements StorageObjectEventProcessor {
    private StorageObjectEvent lastEvent; 
    
    @Override
    public void setFedoraObjectStore(FedoraObjectStore fedoraObjectStore) {
        // no-op
    }

    @Override
    public void process(StorageObjectEvent event) throws OCSException {
        setLastEvent(event);
    }
    
    public StorageObjectEvent getLastEvent() {
        return lastEvent;
    }
    
    public void setLastEvent(StorageObjectEvent event) {
        lastEvent = event;
    }
}
