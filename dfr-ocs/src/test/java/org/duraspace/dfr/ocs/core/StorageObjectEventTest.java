/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
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
        new StorageObjectEvent(null, StorageObjectEvent.EventType.CREATED,
                mockStorageObject);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullType() {
        new StorageObjectEvent("foo", null, mockStorageObject);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullStorageObject() {
        new StorageObjectEvent("foo", StorageObjectEvent.EventType.CREATED, null);
    }

    @Test
    public void getId() {
        StorageObjectEvent event = new StorageObjectEvent("foo",
                StorageObjectEvent.EventType.CREATED, mockStorageObject);
        Assert.assertEquals("foo", event.getEventID());
    }

    @Test
    public void getType() {
        StorageObjectEvent event = new StorageObjectEvent("foo",
                StorageObjectEvent.EventType.CREATED, mockStorageObject);
        Assert.assertEquals(StorageObjectEvent.EventType.CREATED, event.getEventType());
    }

    @Test
    public void getStorageObject() {
        StorageObjectEvent event = new StorageObjectEvent("foo",
                StorageObjectEvent.EventType.CREATED, mockStorageObject);
        Assert.assertEquals(mockStorageObject, event.getStorageObject());
    }
}
