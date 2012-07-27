package org.duraspace.dfr.ocs.core;

import org.mockito.Mockito;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link StorageObjectEvent}.
 */
public class StorageObjectEventTest {
    private StorageObject mockStorageObject = Mockito.mock(StorageObject.class);

    @Test (expected=NullPointerException.class)
    public void initWithNullId() {
        new StorageObjectEvent(null, StorageObjectEvent.Type.CREATED,
                mockStorageObject);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullType() {
        new StorageObjectEvent("foo", null, mockStorageObject);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullStorageObject() {
        new StorageObjectEvent("foo", StorageObjectEvent.Type.CREATED, null);
    }

    @Test
    public void getId() {
        StorageObjectEvent event = new StorageObjectEvent("foo",
                StorageObjectEvent.Type.CREATED, mockStorageObject);
        Assert.assertEquals("foo", event.getId());
    }

    @Test
    public void getType() {
        StorageObjectEvent event = new StorageObjectEvent("foo",
                StorageObjectEvent.Type.CREATED, mockStorageObject);
        Assert.assertEquals(StorageObjectEvent.Type.CREATED, event.getType());
    }

    @Test
    public void getStorageObject() {
        StorageObjectEvent event = new StorageObjectEvent("foo",
                StorageObjectEvent.Type.CREATED, mockStorageObject);
        Assert.assertEquals(mockStorageObject, event.getStorageObject());
    }
}
