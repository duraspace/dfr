package org.duraspace.dfr.sync.controller;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.duracloud.sync.config.SyncToolConfig;
import org.duraspace.dfr.sync.service.SyncToolConfigSerializer;
import org.duraspace.dfr.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class SyncToolConfigSerializerTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSerializeDeserialize() throws IOException {
        SyncToolConfig c1 = new SyncToolConfig();
        String username = "test-username";
        c1.setUsername(username);
        File file = File.createTempFile("synctoolconfig", ".xml");
        file.deleteOnExit();
        SyncToolConfigSerializer.serialize(c1, file.getAbsolutePath());
        SyncToolConfig c2 = SyncToolConfigSerializer.deserialize(file.getAbsolutePath());
        Assert.assertEquals(c1.getUsername(), c2.getUsername());
    }

}
