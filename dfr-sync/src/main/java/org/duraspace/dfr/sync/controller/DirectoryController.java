/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.controller;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.duraspace.dfr.sync.domain.DirectoryConfig;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.duraspace.dfr.sync.setup.DirectoryConfigForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A spring controller for directory creation.
 * 
 * @author Daniel Bernstein
 * 
 */
@Controller
@RequestMapping(ConfigurationController.CONFIGURATION_MAPPING)
public class DirectoryController {
    private static Logger log =
        LoggerFactory.getLogger(DirectoryController.class);

    private SyncConfigurationManager syncConfigurationManager;

    @Autowired
    public DirectoryController(SyncConfigurationManager syncConfigurationManager) {
        this.syncConfigurationManager = syncConfigurationManager;
    }

    @ModelAttribute("directoryConfigForm")
    public DirectoryConfigForm directoryConfigForm() {
        return new DirectoryConfigForm();
    }

    @RequestMapping(value = { "/add" }, method = RequestMethod.GET)
    public String get() {
        log.debug("accessing new directory page");
        return "directory";
    }

    @RequestMapping(value = { "/add" }, method=RequestMethod.POST)
    public String add(@Valid DirectoryConfigForm directoryConfigForm) {
        log.debug("adding new directory");
        DirectoryConfigs directoryConfigs =
            this.syncConfigurationManager.retrieveDirectoryConfigs();

        String path = directoryConfigForm.getDirectoryPath();
        directoryConfigs.add(new DirectoryConfig(path));
        
        this.syncConfigurationManager.persistDirectoryConfigs(directoryConfigs);

        return "success";
    }



}
