/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.sync.controller;

import javax.validation.Valid;

import org.duraspace.dfr.sync.domain.DirectoryConfigForm;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
import org.duraspace.dfr.sync.domain.DuracloudCredentialsForm;
import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
    private static final String FEEDBACK_MESSAGE = "user-feedback";
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

    @ModelAttribute("duracloudCredentialsForm")
    public DuracloudCredentialsForm duracloudCredentials(){
        DuracloudConfiguration c =  this.syncConfigurationManager.retrieveDuracloudConfiguration();
        DuracloudCredentialsForm form = new DuracloudCredentialsForm(c);
        return form;
    }

    @RequestMapping(value = { "" })
    public String get() {
        log.debug("accessing configuration page");
        return "configuration";
    }
    
    @RequestMapping(value = { "/remove" }, method=RequestMethod.POST)
    public View removeDirectory(@Valid DirectoryConfigForm directoryConfigForm) {
        String path = directoryConfigForm.getDirectoryPath();
        
        log.debug("removing path: {}", path);
        DirectoryConfigs directoryConfigs =
            this.syncConfigurationManager.retrieveDirectoryConfigs();

        directoryConfigs.removePath(path);
        this.syncConfigurationManager.persistDirectoryConfigs(directoryConfigs);
        
        //redirectAttributes.addAttribute(FEEDBACK_MESSAGE, "Successfully removed");
        return new RedirectView("/configuration", true, false, false);
    }
}
