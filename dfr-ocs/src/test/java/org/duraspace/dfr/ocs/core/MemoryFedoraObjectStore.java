package org.duraspace.dfr.ocs.core;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A memory-based {@link FedoraObjectStore} to support testing.
 */
public class MemoryFedoraObjectStore implements FedoraObjectStore {
    
    private final Map<String, FedoraObject> fedoraObjectMap =
            new HashMap<String, FedoraObject>();

    @Override
    public void ingest(FedoraObject object) throws OCSException {
        fedoraObjectMap.put(object.pid(), object);
    }

    @Override
    public void purge(String pid) throws OCSException {
        fedoraObjectMap.remove(pid);
    }

    /**
     * Gets the map of objects.
     *
     * @return the pid-keyed map of objects currently in memory.
     */
    public Map<String, FedoraObject> getMap() {
        return fedoraObjectMap;
    }
}
