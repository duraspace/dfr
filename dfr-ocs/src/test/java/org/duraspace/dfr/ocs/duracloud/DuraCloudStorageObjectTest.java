package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.ocs.core.OCSException;
import org.mockito.Mockito;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Unit tests for {@link org.duraspace.dfr.ocs.duracloud.DuraCloudStorageObject}.
 */
public class DuraCloudStorageObjectTest {

    @Test
    public void getId() {
        ContentStore contentStore = Mockito.mock(ContentStore.class);
        Assert.assertEquals(getInstance(contentStore, false).getId(),
            "contentId");
    }

//    @Test
//    public void getContentThenMetadata() throws Exception {
//        getBoth(false);
//    }

//    @Test
//    public void getMetadataThenContent() throws Exception {
//        getBoth(true);
//    }

    private void getBoth(boolean metadataFirst) throws Exception {
        ContentStore contentStore = Mockito.mock(ContentStore.class);
        Content content = Mockito.mock(Content.class);
        Mockito.when(contentStore.getContent("spaceId", "contentId")).
                thenReturn(content);
        Mockito.when(content.getProperties()).thenReturn(
                new HashMap<String, String>());
        Mockito.when(content.getStream()).thenReturn(
                new ByteArrayInputStream(new byte[0]));
        DuraCloudStorageObject o = getInstance(contentStore, false);
        if (metadataFirst) {
            Assert.assertEquals(0, o.getMetadata().size());
            Assert.assertEquals(-1, o.getContent().read());
        } else {
            Assert.assertEquals(-1, o.getContent().read());
            Assert.assertEquals(0, o.getMetadata().size());
        }
        Mockito.verify(contentStore).getContent("spaceId", "contentId");
    }

//    @Test
//    public void getContentThenMetadataDeleted() throws Exception {
//        getBothDeleted(false);
//    }

//    @Test
//    public void getMetadataThenContentDeleted() throws Exception {
//        getBothDeleted(true);
//    }

    private void getBothDeleted(boolean metadataFirst) throws Exception {
        ContentStore contentStore = Mockito.mock(ContentStore.class);
        DuraCloudStorageObject o = getInstance(contentStore, true);
        if (metadataFirst) {
            Assert.assertEquals(0, o.getMetadata().size());
            Assert.assertEquals(null, o.getContent());
        } else {
            Assert.assertEquals(null, o.getContent());
            Assert.assertEquals(0, o.getMetadata().size());
        }
    }

    @Test (expected=OCSException.class)
    public void simulateContentStoreException() throws Exception {
        ContentStore contentStore = Mockito.mock(ContentStore.class);
        Mockito.when(contentStore.getContent("spaceId", "contentId")).
                thenThrow(new ContentStoreException("foo"));
        DuraCloudStorageObject o = getInstance(contentStore, false);
        o.getContent();
        //Mockito.verify(o) TODO: What do we want to test.
    }

    @Test
    public void closeStreamIfNeededOnFinalization() throws Throwable {
        ContentStore contentStore = Mockito.mock(ContentStore.class);
        Content content = Mockito.mock(Content.class);
        Mockito.when(contentStore.getContent("spaceId", "contentId")).
            thenReturn(content);
        Mockito.when(content.getProperties()).thenReturn(
            new HashMap<String, String>());
        InputStream stream = Mockito.mock(InputStream.class);
        stream.close();
        Mockito.when(content.getStream()).thenReturn(stream);
        DuraCloudStorageObject o = getInstance(contentStore, false);
        Assert.assertEquals(0, o.getMetadata().size());
        o.finalize(); // normally not called explicitly; only for test
        //Mockito.verify(stream);  TODO: What do we want to test.
        //Mockito.verify(content);
        //Mockito.verify(contentStore);
    }

    private static DuraCloudStorageObject getInstance(
            ContentStore contentStore, boolean deleted) {
        return new DuraCloudStorageObject(contentStore, "spaceId", "contentId", deleted);
    }

}
