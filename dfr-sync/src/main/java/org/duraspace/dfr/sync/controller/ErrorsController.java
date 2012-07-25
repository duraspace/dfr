/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * A spring controller for error list viewing and navigation
 *
 * @author Daniel Bernstein
 *
 */
@Controller
public class ErrorsController {
    private static Logger log = LoggerFactory.getLogger(ErrorsController.class);
    @RequestMapping(value= {"/errors"})
    public String get(){
        log.debug("accessing errors page");
        return "errors";
    }
}
