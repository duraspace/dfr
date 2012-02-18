package org.duraspace.dfr.ocs.core;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link StorageObjectEvent}.
 */
public class StorageObjectEventTest {
    private StorageObject mockStorageObject =
            EasyMock.createMock(StorageObject.class);

    @Test (expected=NullPointerException.class)
    public void initWithNullType() {
        new StorageObjectEvent(null, mockStorageObject);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullStorageObject() {
        new StorageObjectEvent(StorageObjectEvent.Type.CREATED, null);
    }

    @Test
    public void getType() {
        StorageObjectEvent event = new StorageObjectEvent(
                StorageObjectEvent.Type.CREATED, mockStorageObject);
        Assert.assertEquals(StorageObjectEvent.Type.CREATED, event.getType());
    }

    @Test
    public void getStorageObject() {
        StorageObjectEvent event = new StorageObjectEvent(
                StorageObjectEvent.Type.CREATED, mockStorageObject);
        Assert.assertEquals(mockStorageObject, event.getStorageObject());
    }
}
