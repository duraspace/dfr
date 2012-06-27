/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * A spring controller for status related functions.
 *
 * @author Daniel Bernstein
 *
 */
@Controller
public class StatusController {
    private static Logger log = LoggerFactory.getLogger(StatusController.class);
    @RequestMapping(value= {"/status"})
    public String get(){
        log.debug("accessing status page");
        return "status";
    }
}
