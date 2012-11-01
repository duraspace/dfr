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
