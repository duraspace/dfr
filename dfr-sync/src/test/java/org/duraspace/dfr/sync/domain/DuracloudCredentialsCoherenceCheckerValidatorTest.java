/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.domain;

import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.sync.domain.DuracloudCredentialsCoherenceCheckerValidator;
import org.duraspace.dfr.sync.domain.DuracloudCredentialsForm;
import org.duraspace.dfr.sync.service.ContentStoreFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public class DuracloudCredentialsCoherenceCheckerValidatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testIsValid() throws Exception{
        ContentStoreFactory f = EasyMock.createMock(ContentStoreFactory.class);
        DuracloudCredentialsForm credentials = new DuracloudCredentialsForm();
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
        EasyMock.expect(f.create(credentials)).andReturn(contentStore);
        EasyMock.replay(f, contentStore);
        DuracloudCredentialsCoherenceCheckerValidator v = new DuracloudCredentialsCoherenceCheckerValidator(f);
        Assert.assertTrue(v.isValid(credentials, null));
        EasyMock.verify(f, contentStore);
    }

    @Test
    public void testIsInvalid() throws Exception{
        ContentStoreFactory f = EasyMock.createMock(ContentStoreFactory.class);
        DuracloudCredentialsForm credentials = new DuracloudCredentialsForm();
        EasyMock.expect(f.create(credentials)).andThrow(new ContentStoreException("test exception"));
        EasyMock.replay(f);
        DuracloudCredentialsCoherenceCheckerValidator v = new DuracloudCredentialsCoherenceCheckerValidator(f);
        Assert.assertFalse(v.isValid(credentials, null));
        EasyMock.verify(f);
    }

}
