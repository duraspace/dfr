/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

/**
 * 
 * @author Daniel Bernstein 
 * 
 */
@Component(SetupFlowHandler.FLOW_ID)
public class SetupFlowHandler extends AbstractFlowHandler {
    public static final String FLOW_ID = "setup";
    @Override
    public String getFlowId() {
        return FLOW_ID;
    }
    
    @Override
    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        return "contextRelative:/";
    }
}
