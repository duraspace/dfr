/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.sync.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class LogControllerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStatus() {
        LogController c = new LogController();
        String s = c.get();
        Assert.assertNotNull(s);
    }

}
