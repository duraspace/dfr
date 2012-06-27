/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */package org.duraspace.dfr.sync.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class StatusControllerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStatus() {
        StatusController sc = new StatusController();
        String s = sc.get();
        Assert.assertNotNull(s);
    }

}
