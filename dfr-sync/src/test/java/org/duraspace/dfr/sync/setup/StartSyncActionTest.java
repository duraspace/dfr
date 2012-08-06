/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */package org.duraspace.dfr.sync.setup;

import org.duraspace.dfr.sync.service.SyncProcessManager;
import org.duraspace.dfr.test.AbstractTest;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class StartSyncActionTest extends AbstractTest {

    @Test
    public void testExecute() throws Exception {
        SyncProcessManager spm = createMock(SyncProcessManager.class);
        spm.start();
        EasyMock.expectLastCall();
        RequestContext context = createMock(RequestContext.class);
        
        replay();
        
        StartSyncAction a = new StartSyncAction(spm);
        
        Event result = a.execute(context);
        
        Assert.assertEquals("success", result.getId());
        
    }

}
