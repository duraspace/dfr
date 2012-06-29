/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.selenium;

import org.junit.Test;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public class TestStatusPage extends BaseSeleniumTest {
    @Test
    public void testGet(){
        sc.open(getAppRoot()+"/status");
    }
}
