/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;

/**
 * Provides persist operations for configuration related operations.
 * 
 * @author Daniel Bernstein
 * 
 */
public interface SyncConfigurationManager {

    
    /**
     * 
     * @param username
     * @param password
     * @param host
     * @param port
     * @param spaceId
     */
    public void persistDuracloudConfiguration(String username,
                                            String password,
                                            String host,
                                            int port,
                                            String spaceId);

    /**
     * 
     * @return
     */
    public DuracloudConfiguration retrieveDuracloudConfiguration();

    
    /**
     * 
     * @return
     */
    public DirectoryConfigs retrieveDirectoryConfigs();
    
    /**
     * 
     * @return
     */
    public boolean isConfigurationComplete();
    
    
}