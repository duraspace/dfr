/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
import org.springframework.stereotype.Component;

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
                                            String port,
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

    public void persistDirectoryConfigs(DirectoryConfigs configs);

    public void setWorkingDirectory(String workingDirectory);

    public void setConfigXmlPath(String configXml);
    
    public void persist();

    public void purgeWorkDirectory();
    
}
