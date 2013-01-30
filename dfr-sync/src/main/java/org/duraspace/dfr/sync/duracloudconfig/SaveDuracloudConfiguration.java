/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.sync.duracloudconfig;

import org.duraspace.dfr.sync.domain.DuracloudCredentialsForm;
import org.duraspace.dfr.sync.domain.SpaceForm;
import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
@Component
public class SaveDuracloudConfiguration {

    private static Logger log = LoggerFactory.getLogger(SaveDuracloudConfiguration.class);
    private SyncConfigurationManager syncConfigurationManager;

    @Autowired
    public SaveDuracloudConfiguration(
        @Qualifier("syncConfigurationManager") SyncConfigurationManager syncConfigurationManager) {
        this.syncConfigurationManager = syncConfigurationManager;
    }

    public String execute(DuracloudCredentialsForm credentials,
                          SpaceForm spaceForm) {
        syncConfigurationManager.persistDuracloudConfiguration(credentials.getUsername(),
                                                               credentials.getPassword(),
                                                               credentials.getHost(),
                                                               credentials.getPort(),
                                                               spaceForm.getSpaceId());

        log.info("successfully saved duracloud configuration.");
        return "success";
    }
}
