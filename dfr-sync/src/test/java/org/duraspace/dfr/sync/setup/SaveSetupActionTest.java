/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */package org.duraspace.dfr.sync.setup;

import org.duraspace.dfr.sync.controller.SetupHelper;
import org.duraspace.dfr.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class SaveSetupActionTest extends AbstractTest {

    @Test
    public void testExecute() throws Exception {
        SaveSetupAction sc = new SaveSetupAction();
        RequestContext requestContext = createMock(RequestContext.class);
        SetupHelper setupHelper = createMock(SetupHelper.class);
        setupHelper.setComplete(true);
        sc.setSetupHelper(setupHelper);
        
        replay();
        
        Event result = sc.execute(requestContext);
        Assert.assertEquals("success", result.getId());
        
    }

}
