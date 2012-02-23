package org.duraspace.dfr.ocs.duracloud;

import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.ocs.core.OCSException;
import org.easymock.EasyMock;
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
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
        Assert.assertEquals(getInstance(contentStore, false).getId(),
                "contentId");
    }

    @Test
    public void getContentThenMetadata() throws Exception {
        getBoth(false);
    }

    @Test
    public void getMetadataThenContent() throws Exception {
        getBoth(true);
    }

    private void getBoth(boolean metadataFirst) throws Exception {
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
        Content content = EasyMock.createMock(Content.class);
        EasyMock.expect(contentStore.getContent("spaceId", "contentId")).
                andReturn(content);
        EasyMock.expect(content.getProperties()).andReturn(
                new HashMap<String, String>());
        EasyMock.expect(content.getStream()).andReturn(
                new ByteArrayInputStream(new byte[0]));
        EasyMock.replay(content, contentStore);
        DuraCloudStorageObject o = getInstance(contentStore, false);
        if (metadataFirst) {
            Assert.assertEquals(0, o.getMetadata().size());
            Assert.assertEquals(-1, o.getContent().read());
        } else {
            Assert.assertEquals(-1, o.getContent().read());
            Assert.assertEquals(0, o.getMetadata().size());
        }
        EasyMock.verify(content, contentStore);
    }

    @Test
    public void getContentThenMetadataDeleted() throws Exception {
        getBothDeleted(false);
    }

    @Test
    public void getMetadataThenContentDeleted() throws Exception {
        getBothDeleted(true);
    }
        
    private void getBothDeleted(boolean metadataFirst) throws Exception {
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
        EasyMock.replay(contentStore);
        DuraCloudStorageObject o = getInstance(contentStore, true);
        if (metadataFirst) {
            Assert.assertEquals(0, o.getMetadata().size());
            Assert.assertEquals(null, o.getContent());
        } else {
            Assert.assertEquals(null, o.getContent());
            Assert.assertEquals(0, o.getMetadata().size());
        }
        EasyMock.verify(contentStore);
    }

    @Test (expected=OCSException.class)
    public void simulateContentStoreException() throws Exception {
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
        EasyMock.expect(contentStore.getContent("spaceId", "contentId")).
                andThrow(new ContentStoreException("foo"));
        EasyMock.replay(contentStore);
        DuraCloudStorageObject o = getInstance(contentStore, false);
        o.getContent();
    }
    
    @Test
    public void closeStreamIfNeededOnFinalization() throws Throwable {
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);
        Content content = EasyMock.createMock(Content.class);
        EasyMock.expect(contentStore.getContent("spaceId", "contentId")).
                andReturn(content);
        EasyMock.expect(content.getProperties()).andReturn(
                new HashMap<String, String>());
        InputStream stream = EasyMock.createMock(InputStream.class);
        stream.close();
        EasyMock.expect(content.getStream()).andReturn(stream);
        EasyMock.replay(stream, content, contentStore);
        DuraCloudStorageObject o = getInstance(contentStore, false);
        Assert.assertEquals(0, o.getMetadata().size());
        o.finalize(); // normally not called explicity; only for test
        EasyMock.verify(stream, content, contentStore);
    }

    private static DuraCloudStorageObject getInstance(
            ContentStore contentStore, boolean deleted) {
        return new DuraCloudStorageObject(contentStore, "spaceId", "contentId",
                new HashMap<String, String>(), deleted);
    }
}
