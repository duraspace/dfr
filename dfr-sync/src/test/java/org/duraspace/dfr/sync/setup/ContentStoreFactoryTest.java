/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class ContentStoreFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreate() throws ContentStoreException {
        final ContentStoreManager csm =
            EasyMock.createMock(ContentStoreManager.class);
        ContentStore cs = EasyMock.createMock(ContentStore.class);
        csm.login(EasyMock.isA(Credential.class));
        EasyMock.expectLastCall();
        EasyMock.expect(csm.getPrimaryContentStore()).andReturn(cs);
        ContentStoreFactory csf = new ContentStoreFactory() {
            @Override
            protected ContentStoreManager
                createContentStoreManager(String host, String port) {
                return csm;
            }
        };

        EasyMock.replay(csm, cs);
        DuracloudCredentialsForm dcf = new DuracloudCredentialsForm();
        dcf.setHost("test");
        dcf.setUsername("username");
        dcf.setPassword("password");
        Assert.assertNotNull(csf.create(dcf));
        EasyMock.verify(csm, cs);
    }

    @Test
    public void testCreateFailure() throws ContentStoreException {
        final ContentStoreManager csm =
            EasyMock.createMock(ContentStoreManager.class);
        ContentStore cs = EasyMock.createMock(ContentStore.class);
        csm.login(EasyMock.isA(Credential.class));
        EasyMock.expectLastCall();
        EasyMock.expect(csm.getPrimaryContentStore())
                .andThrow(new ContentStoreException("test exception"));
        ContentStoreFactory csf = new ContentStoreFactory() {
            @Override
            protected ContentStoreManager
                createContentStoreManager(String host, String port) {
                return csm;
            }
        };

        EasyMock.replay(csm, cs);
        DuracloudCredentialsForm dcf = new DuracloudCredentialsForm();
        dcf.setHost("test");
        dcf.setUsername("username");
        dcf.setPassword("password");
        try{
            csf.create(dcf);
            Assert.assertTrue(false);
        }catch(ContentStoreException ex){
            Assert.assertTrue(true);
        }

        EasyMock.verify(csm, cs);
    }

}
