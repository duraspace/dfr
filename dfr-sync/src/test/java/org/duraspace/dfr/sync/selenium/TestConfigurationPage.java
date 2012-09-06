/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.selenium;

import org.junit.Assert;
import org.junit.Test;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public class TestConfigurationPage extends BasePostSetupPage {
    @Test
    public void testGet(){
        sc.open(getAppRoot()+"/configuration");
        Assert.assertTrue(isElementPresent("css=#watched-directories"));
        Assert.assertTrue(isTextPresent(uploadDir.getAbsolutePath()));

        Assert.assertTrue(isElementPresent("css=#duracloud-configuration"));
        Assert.assertTrue(isTextPresent(props.getProperty("username")));

    }
    
    @Test
    public void testAdd() throws Exception{
        sc.open(getAppRoot()+"/configuration");
        Assert.assertTrue(isElementPresent("css=#add"));
        sc.click("css=#add");
        Thread.sleep(1000);
        Assert.assertTrue(sc.isVisible("css=#directoryConfigForm"));
        sc.click("css=#cancel");
        Assert.assertFalse(sc.isVisible("css=#directoryConfigForm"));

    }

}
