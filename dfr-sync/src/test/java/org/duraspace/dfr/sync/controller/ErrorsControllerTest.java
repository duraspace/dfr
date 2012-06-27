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
public class ErrorsControllerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStatus() {
        ErrorsController c = new ErrorsController();
        String s = c.get();
        Assert.assertNotNull(s);
    }

}
