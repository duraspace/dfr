/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.selenium;

import java.io.File;

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
    public void testAddCancel() throws Exception{
        sc.open(getAppRoot()+"/configuration");
        Assert.assertTrue(isElementPresent("css=#add"));
        sc.click("css=#add");
        Thread.sleep(2000);
        Assert.assertTrue(sc.isVisible("css=#directoryConfigForm"));
        sc.click("css=#cancel");
        Thread.sleep(500);
        Assert.assertFalse(sc.isVisible("css=#directoryConfigForm"));
    }

    @Test
    public void testAddAndRemoveDirectory() throws Exception{
        
        File testDir =
            new File(System.getProperty("java.io.tmpdir")
                + File.separator + System.currentTimeMillis());
        
        testDir.mkdirs();
        testDir.deleteOnExit();
        
        sc.open(getAppRoot()+"/configuration");
        Assert.assertTrue(isElementPresent("css=#add"));
        sc.click("css=#add");
        Thread.sleep(2000);
        Assert.assertTrue(sc.isVisible("css=#directoryConfigForm"));

        String[] list = testDir.getAbsolutePath().split(File.separator);
        String path = "/";
        
        for(String dir : list){
            if("".equals(dir)) continue;
            
            path += dir + "/";
             String pathSelector = "css=a[rel='"+path+"']";
             log.debug("checking if " + pathSelector + " is present");
             Assert.assertTrue(isElementPresent(pathSelector));
             sc.click(pathSelector);
             Thread.sleep(2000);
        }
        sc.click("css=#directoryConfigForm #add");
        Thread.sleep(500);
        Assert.assertFalse(sc.isElementPresent("css=#directoryConfigForm"));
        Assert.assertTrue(sc.isTextPresent(testDir.getAbsolutePath()));
        String removeButton = "css=#" + testDir.getName() + "-remove";
        Assert.assertTrue(sc.isElementPresent(removeButton));
        clickAndWait(removeButton);
        Assert.assertFalse(sc.isElementPresent(removeButton));

    }
    

    @Test
    public void testEditEnterInvalidDataCancelDuracloudConfig() throws Exception{
        sc.open(getAppRoot()+"/configuration");
        Assert.assertTrue(isElementPresent("css=#edit"));
        sc.click("css=#edit");
        Thread.sleep(2000);
        Assert.assertTrue(sc.isVisible("css=#duracloudCredentialsForm"));

        sc.type("css=#username", "");
        sc.click("css=#next");
        Thread.sleep(2000);
        Assert.assertTrue(sc.isVisible("css=#duracloudCredentialsForm #username.error"));

        sc.click("css=#cancel");
        Thread.sleep(500);
        Assert.assertFalse(sc.isVisible("css=#duracloudCredentialsForm"));

    }
}
