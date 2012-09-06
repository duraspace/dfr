/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.domain;

import junit.framework.Assert;

import org.duraspace.dfr.test.AbstractTest;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class DirectoryConfigsTest extends AbstractTest {

    
    @Test
    public void testRemovePath(){
        DirectoryConfigs dc = new DirectoryConfigs();
        String testPath = "path";
        dc.add(new DirectoryConfig(testPath));
        Assert.assertEquals(1, dc.size());
        dc.removePath(testPath);
        Assert.assertEquals(0, dc.size());
        
    }

}
