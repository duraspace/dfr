/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
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
public class ContentStoreManagerFactoryImplTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        super.setup();
    }

    @Test
    public void testCreate() throws ContentStoreException {

        SyncConfigurationManager syncConfigurationManager =
            createMock(SyncConfigurationManager.class);

        DuracloudConfiguration dc = createMock(DuracloudConfiguration.class);

        EasyMock.expect(syncConfigurationManager.retrieveDuracloudConfiguration())
                .andReturn(dc);
        EasyMock.expect(dc.getHost()).andReturn("host");
        EasyMock.expect(dc.getPort()).andReturn(8443);

        replay();

        ContentStoreManagerFactoryImpl csf =
            new ContentStoreManagerFactoryImpl(syncConfigurationManager);

        Assert.assertNotNull(csf.create());

    }

}
