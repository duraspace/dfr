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

    private final Map<String, String> logMessageMap =
            new HashMap<String, String>();

    @Override
    public boolean ingest(FedoraObject object, String logMessage)
            throws OCSException {
        if (fedoraObjectMap.containsKey(object.pid())) {
            return false;
        }
        fedoraObjectMap.put(object.pid(), object);
        logMessageMap.put(object.pid(), logMessage);
        return true;
    }

    @Override
    public void purge(String pid, String logMessage) throws OCSException {
        fedoraObjectMap.remove(pid);
        logMessageMap.put(pid, logMessage);
    }
    
    @Override
    public FedoraObject export(String pid) throws OCSException {
        return fedoraObjectMap.get(pid);
    }

    /**
     * Gets the map of objects.
     *
     * @return the pid-keyed map of objects currently in memory.
     */
    public Map<String, FedoraObject> getFedoraObjectMap() {
        return fedoraObjectMap;
    }

    /**
     * Gets the map of log messages.
     *
     * @return the pid-keyed map of log messages currently in memory.
     */
    public Map<String, String> getLogMessageMap() {
        return logMessageMap;
    }
}
