/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.controller;

import javax.validation.Valid;

import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.duraspace.dfr.sync.setup.DirectoryConfigForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String post(@Valid DirectoryConfigForm directoryConfigForm) {
        log.debug("adding new directory");
        return "success";
    }
}
