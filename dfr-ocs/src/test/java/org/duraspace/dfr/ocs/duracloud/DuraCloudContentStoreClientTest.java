package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.common.model.Credential;
import org.mockito.Mockito;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link DuraCloudContentStoreClient}.
 */
public class DuraCloudContentStoreClientTest {

    @Test (expected=NullPointerException.class)
    public void initWithNullContentManager() {
        new DuraCloudContentStoreClient(Mockito.mock(ContentStoreManager.class), null);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullCredential() {
        new DuraCloudContentStoreClient(null, Mockito.mock(Credential.class));
    }

}
