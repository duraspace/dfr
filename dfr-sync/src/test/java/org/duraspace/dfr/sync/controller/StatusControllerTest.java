/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */package org.duraspace.dfr.sync.controller;

import org.duraspace.dfr.sync.service.SyncProcessException;
import org.duraspace.dfr.sync.service.SyncProcessManager;
import org.duraspace.dfr.test.AbstractTest;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.View;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class StatusControllerTest extends AbstractTest {

    private SyncProcessManager syncProcessManager;
    private StatusController statusController;
    @Before
    @Override
    public void setup() {
        super.setup();
        syncProcessManager = createMock(SyncProcessManager.class);
        statusController = new StatusController(syncProcessManager);
    }
    
    @Test
    public void testStatus() {
        replay();
        
        String s = statusController.get();
        Assert.assertNotNull(s);
    }

    
    @Test
    public void testStart() throws SyncProcessException{
        syncProcessManager.start();
        EasyMock.expectLastCall();
        replay();
        
        View v = statusController.start();
        Assert.assertNotNull(v);
    }

    @Test
    public void testResume() throws SyncProcessException{
        syncProcessManager.resume();
        EasyMock.expectLastCall();
        replay();
        
        View v = statusController.resume();
        Assert.assertNotNull(v);
    }

    @Test
    public void testPause() throws SyncProcessException {
        syncProcessManager.pause();
        EasyMock.expectLastCall();
        replay();
        
        View v = statusController.pause();
        Assert.assertNotNull(v);
    }

    @Test
    public void testStop() {
        syncProcessManager.stop();
        EasyMock.expectLastCall();
        replay();
        
        View v = statusController.stop();
        Assert.assertNotNull(v);
    }

 
 
}
