/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import org.duraspace.dfr.sync.controller.SetupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * 
 * @author Daniel Bernstein 
 * 
 */
@Component
public class SaveSetupAction extends AbstractAction {
  
    private static Logger log = LoggerFactory.getLogger(SaveSetupAction.class);
    
    @Autowired
    private SetupHelper setupHelper;
    
    public Event doExecute(RequestContext context) throws Exception {
        log.debug("executing...");
        setupHelper.setComplete(true);
        return success();
    }
    
    public void setSetupHelper(SetupHelper setupHelper){
        this.setupHelper = setupHelper;
    }
}
