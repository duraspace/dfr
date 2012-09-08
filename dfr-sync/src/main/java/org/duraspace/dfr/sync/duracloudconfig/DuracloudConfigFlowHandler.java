/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.duracloudconfig;

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
@Component(DuracloudConfigFlowHandler.FLOW_ID)
public class DuracloudConfigFlowHandler extends AbstractFlowHandler {
    public static final String FLOW_ID = "duracloud-config";
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
