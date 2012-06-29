/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AddDirectoryConfigAction extends AbstractAction {
  
    private static Logger log = LoggerFactory.getLogger(AddDirectoryConfigAction.class);
    
    public Event doExecute(RequestContext context) throws Exception {
        log.debug("executing...");
        return success();
    }
}
