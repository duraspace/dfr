/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.core;

import java.util.List;

/**
 * Represents the most most basic event id OCS. Since events provide important
 * control and provenance information this will be built out over time. Likely
 * it will extend into storage objects (or parts thereof) over time.
 */
public interface OCSEvent {

    // Note: There needs to be a more metadata mechanism for all events and
    // required by all of them. For now, implementing class may specifiy their
    // own (usually string-string name value pairs.  This is good enough
    // for now.
    // For example, this could also be a stream. Or it could better fit into
    // a graph-based, resource-representation architecture (using another node
    // for metadata has interesting possibilities. DWD.

    /**
     * Gets the id of the event which is never null and unique within a
     * specified context, possibly persistent and global.
     *
     * @return the id of the event, never <code>null</code> context unique.
     */
    public String getEventID();

    /**
     * Gets a list of events related to this event.
     *
     * @return a <code>List</code> of related events (never null)
     */
    public List<OCSEvent> getRelatedEvents();

}
