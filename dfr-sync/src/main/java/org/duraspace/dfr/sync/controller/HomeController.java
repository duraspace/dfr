/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * A spring controller for the welcome page.
 *
 * @author Daniel Bernstein
 *
 */
@Controller
public class HomeController {
    private static Logger log = LoggerFactory.getLogger(HomeController.class);

    @Autowired SetupHelper setupHelper;

    @RequestMapping(value= {"/"})
    public String get(){
        log.debug("accessing welcome page");
        
        if(isSetupComplete()){
            return "redirect:/status";
        }else{
            return "redirect:/setup";
        }
    }
    
    private boolean isSetupComplete() {
        return this.setupHelper.isComplete();
    }
}
