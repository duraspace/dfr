/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
@Component
public class SaveSetupAction {

    private static Logger log = LoggerFactory.getLogger(SaveSetupAction.class);
    private SyncConfigurationManager syncConfigurationManager;

    @Autowired
    public SaveSetupAction(SyncConfigurationManager syncConfigurationManager) {
        this.syncConfigurationManager = syncConfigurationManager;
    }

    public String execute(DuracloudCredentialsForm credentials,
                          SpaceForm spaceForm,
                          DirectoryConfigs configs) throws Exception {
        syncConfigurationManager.persistDuracloudConfiguration(credentials.getUsername(),
                                                               credentials.getPassword(),
                                                               credentials.getHost(),
                                                               credentials.getPort(),
                                                               spaceForm.getSpaceId());
        syncConfigurationManager.persistDirectoryConfigs(configs);
        
        return "success";
    }
}
