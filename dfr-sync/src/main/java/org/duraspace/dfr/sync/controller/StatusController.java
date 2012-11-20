/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.controller;

import java.io.File;
import java.util.List;

import org.duracloud.sync.endpoint.MonitoredFile;
import org.duracloud.sync.mgmt.SyncSummary;
import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.duraspace.dfr.sync.domain.SyncProcessStats;
import org.duraspace.dfr.sync.service.SyncProcessError;
import org.duraspace.dfr.sync.service.SyncProcessException;
import org.duraspace.dfr.sync.service.SyncProcessManager;
import org.duraspace.dfr.sync.util.FileSizeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * A spring controller for status related functions.
 * 
 * @author Daniel Bernstein
 * 
 */
@Controller
@RequestMapping(StatusController.STATUS_MAPPING)
public class StatusController {
    public static final String STATUS_MAPPING = "/status";
    public static final String ACTIVE_UPLOADS_KEY = "activeUploads";
    private static Logger log = LoggerFactory.getLogger(StatusController.class);

    private SyncProcessManager syncProcessManager;

    @Autowired
    public StatusController(SyncProcessManager syncProcessManager) {
        this.syncProcessManager = syncProcessManager;
    }



    @RequestMapping(value = { "" })
    public String get(@RequestParam(required=false, defaultValue="queued") String statusTab, Model model) {
        log.debug("accessing status page");
        model.addAttribute("statusTab", statusTab);
        return "status";
    }

    @RequestMapping(value = StatusController.STATUS_MAPPING, method = RequestMethod.POST, params = { "start" })
    public View
        start() {
        try {
            this.syncProcessManager.start();
        } catch (SyncProcessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return redirectTo(STATUS_MAPPING);
    }

    protected View redirectTo(String path) {
        RedirectView redirectView = new RedirectView(path, true);
        redirectView.setExposeModelAttributes(false);
        return redirectView;
    }

    @RequestMapping(value = STATUS_MAPPING, method = RequestMethod.POST, params = { "pause" })
    public View
        pause() {
        this.syncProcessManager.pause();
        return redirectTo(StatusController.STATUS_MAPPING);
    }

    @RequestMapping(value = STATUS_MAPPING, method = RequestMethod.POST, params = { "resume" })
    public View
        resume() {
        try {
            this.syncProcessManager.resume();
        } catch (SyncProcessException e) {
            e.printStackTrace();
        }
        return redirectTo(STATUS_MAPPING);
    }

    @RequestMapping(value = STATUS_MAPPING, method = RequestMethod.POST, params = { "stop" })
    public View
        stop() {
            this.syncProcessManager.stop();
        return redirectTo(StatusController.STATUS_MAPPING);
    }

    @ModelAttribute
    public SyncProcessStats syncProcessStats() {
        return this.syncProcessManager.getProcessStats();
    }

    @ModelAttribute
    public SyncProcessState syncProcessState() {
        return this.syncProcessManager.getProcessState();
    }

    @ModelAttribute("currentError")
    public SyncProcessError currentError() {
        return this.syncProcessManager.getError();
    }

    @ModelAttribute("monitoredFiles")
    public List<MonitoredFile> monitoredFiles() {
        return this.syncProcessManager.getMonitoredFiles();
    }

    @ModelAttribute("failures")
    public List<SyncSummary> failures() {
        return this.syncProcessManager.getFailures();
    }

    @ModelAttribute("recentlyCompleted")
    public List<SyncSummary> recentlyCompleted() {
        return this.syncProcessManager.getRecentlyCompleted();
    }

    
    @ModelAttribute("queuedFiles")
    public List<File> queuedFiles() {
        return this.syncProcessManager.getQueuedFiles();
    }
    
    private static FileSizeFormatter fileSizeFormatter = new FileSizeFormatter();
    @ModelAttribute("fileSizeFormatter")
    public FileSizeFormatter fileSizeFormatter(){
        return fileSizeFormatter;
    }

}
