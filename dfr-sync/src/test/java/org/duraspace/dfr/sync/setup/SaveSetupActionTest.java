/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.sync.setup;

import org.duraspace.dfr.sync.domain.DirectoryConfig;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudCredentialsForm;
import org.duraspace.dfr.sync.domain.SpaceForm;
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
