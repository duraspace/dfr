package org.duraspace.dfr.ocs.core;

/**
 * Listens for {@link StorageObjectEvent}s and sends them to a
 * {@link StorageObjectEventProcessor}.
 */
public interface StorageObjectEventListener {

    /**
     * Sets the processor to which events should be sent.
     *
     * @param processor the processor, never <code>null</code>.
     */
    void setProcessor(StorageObjectEventProcessor processor);

    /**
     * Flushes any enqueued events to the processor and releases all resources
     * held by this listener. This can be safely called multiple times.
     */
    void close();
}
