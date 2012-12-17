/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.common.model.Credential;
import org.mockito.Mockito;
import org.junit.Test;

/**
 * Unit tests for {@link DuraCloudObjectStoreClient}.
 */
public class DuraCloudObjectStoreClientTest {

    @Test (expected=NullPointerException.class)
    public void initWithNullContentManager() {
        new DuraCloudObjectStoreClient(Mockito.mock(ContentStoreManager.class), null);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullCredential() {
        new DuraCloudObjectStoreClient(null, Mockito.mock(Credential.class));
    }

}
