/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duraspace.dfr.sync.service;

import java.io.File;

import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
import org.duraspace.dfr.test.AbstractTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class SyncConfigurationManagerImplTest extends AbstractTest {
    private SyncConfigurationManager syncConfigurationManager;
    private String configPath;

    @Before
    public void setUp() throws Exception {
        super.setup();
        configPath = System.getProperty("java.io.tmpdir")
            + File.separator + ".dfr-sync-config";
        
        System.setProperty("dfr.config.path", configPath);
        syncConfigurationManager = new SyncConfigurationManagerImpl();
    }
    
    @Override
    public void tearDown() {
        new File(configPath).delete();
        super.tearDown();
    }

    @Test
    public void testIsConfigurationCompleteFalse() {

        Assert.assertFalse(this.syncConfigurationManager.isConfigurationComplete());
    }

    @Test
    public void testPersistDuracloudConfiguration() {

        String username = "username", password = "password", host =
            "host.duracloud.org", spaceId = "test-space-id", port = "";

        this.syncConfigurationManager.persistDuracloudConfiguration(username,
                                                                    password,
                                                                    host,
                                                                    port,
                                                                    spaceId);
    }

    @Test
    public void testRetrieveDirectoryConfigs() {
        DirectoryConfigs directoryConfigs =
            this.syncConfigurationManager.retrieveDirectoryConfigs();
        Assert.assertNotNull(directoryConfigs);
    }

    @Test
    public void testRetrieveDuracloudConfiguration() {
        DuracloudConfiguration dc =
            this.syncConfigurationManager.retrieveDuracloudConfiguration();
        Assert.assertNotNull(dc);
    }

}
