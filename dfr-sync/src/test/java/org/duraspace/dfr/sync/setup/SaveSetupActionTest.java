/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */package org.duraspace.dfr.sync.setup;

import org.duraspace.dfr.sync.domain.DirectoryConfig;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.service.SyncConfigurationManager;
import org.duraspace.dfr.test.AbstractTest;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class SaveSetupActionTest extends AbstractTest {

    @Test
    public void testExecute() throws Exception {
        DirectoryConfigs configs = new DirectoryConfigs();
        configs.add(new DirectoryConfig("/test"));
        SpaceForm space = new SpaceForm();

        SyncConfigurationManager scm =
            createMock(SyncConfigurationManager.class);
        
        String username = "username";
        String password = "password";
        String host = "host";
        String spaceId = "spaceId";
        DuracloudCredentialsForm cred = new DuracloudCredentialsForm();
        cred.setUsername(username);
        cred.setPassword(password);
        cred.setHost(host);
        space.setSpaceId(spaceId);
        
        scm.persistDuracloudConfiguration(username, password, host, null, spaceId);
        EasyMock.expectLastCall();
        
        scm.persistDirectoryConfigs(configs);
        EasyMock.expectLastCall();
        SaveSetupAction sc = new SaveSetupAction(scm);
        replay();

        String result = sc.execute(cred, space, configs);

        Assert.assertEquals("success", result);

    }

}
