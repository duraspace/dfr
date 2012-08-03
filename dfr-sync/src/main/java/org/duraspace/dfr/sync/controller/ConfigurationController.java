/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.controller;

import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A spring controller for configuration related functions.
 * 
 * @author Daniel Bernstein
 * 
 */
@Controller
@RequestMapping(ConfigurationController.CONFIGURATION_MAPPING)
public class ConfigurationController {
    public static final String CONFIGURATION_MAPPING = "/configuration";
    private static Logger log =
        LoggerFactory.getLogger(ConfigurationController.class);

    private SyncConfigurationManager syncConfigurationManager;

    @Autowired
    public ConfigurationController(
        SyncConfigurationManager syncConfigurationManager) {
        this.syncConfigurationManager = syncConfigurationManager;
    }

    @ModelAttribute("directoryConfigs")
    public DirectoryConfigs directoryConfigs(){
        return this.syncConfigurationManager.retrieveDirectoryConfigs();
    }
    
    @ModelAttribute("duracloudConfiguration")
    public DuracloudConfiguration duracloudConfiguration(){
        return this.syncConfigurationManager.retrieveDuracloudConfiguration();
    }
    
    @RequestMapping(value = { "" })
    public String get() {
        log.debug("accessing configuration page");
        return "configuration";
    }
}
