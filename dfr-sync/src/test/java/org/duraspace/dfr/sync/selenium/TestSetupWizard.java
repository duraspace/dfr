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
public class TestSetupWizard extends BaseSeleniumTest {
    @Test
    public void test(){
        sc.open(getAppRoot()+"/");
        //welcome screen
        Assert.assertTrue(isTextPresent("Welcome"));
        Assert.assertTrue(isElementPresent("id=next"));
        clickAndWait("id=next");

        //enter credentials screen
        Assert.assertTrue(isTextPresent("Username"));
        clickAndWait("id=next");

        //select space
        Assert.assertTrue(isTextPresent("Space"));
        Assert.assertTrue(isElementPresent("id=spaceId"));
        clickAndWait("id=next");

        //add a directory
        Assert.assertTrue(isElementPresent("id=directoryPath"));
        clickAndWait("id=add");

        //directory list
        Assert.assertTrue(isTextPresent("Directories"));
        clickAndWait("id=add");

        //add a directory
        Assert.assertTrue(isElementPresent("id=directoryPath"));
        clickAndWait("id=add");

        //save setup
        Assert.assertTrue(isTextPresent("Directories"));
        clickAndWait("id=next");

        //start Syncing
        Assert.assertTrue(isElementPresent("id=startNow"));
        clickAndWait("id=startNow");

        //verify status page is showing
        Assert.assertTrue(isTextPresent("Status"));

    }
}
