/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */package org.duraspace.dfr.sync.controller;

import org.duraspace.dfr.sync.domain.DirectoryConfig;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.duraspace.dfr.sync.setup.DirectoryConfigForm;
import org.duraspace.dfr.test.AbstractTest;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class DirectoryControllerTest extends AbstractTest {

    private DirectoryController directoryController; 
    private SyncConfigurationManager syncConfigurationManager;

    @Before
    @Override
    public void setup() {
        super.setup();

        this.syncConfigurationManager = createMock(SyncConfigurationManager.class);
        this.directoryController = new DirectoryController(syncConfigurationManager);
    }
    
    @Test
    public void testGet() {
        replay();
        Assert.assertNotNull(directoryController.get());
    }
    
    @Test
    public void testAdd() {
        String testPath = "testPath";
        DirectoryConfigs configs = createMock(DirectoryConfigs.class);
        EasyMock.expect(this.syncConfigurationManager.retrieveDirectoryConfigs()).andReturn(configs);
        EasyMock.expect(configs.add(EasyMock.isA(DirectoryConfig.class))).andReturn(true);
        DirectoryConfigForm f = new DirectoryConfigForm();
        f.setDirectoryPath(testPath);
        this.syncConfigurationManager.persistDirectoryConfigs(configs);
        replay();
        Assert.assertNotNull(directoryController.add(f));
    }
}
