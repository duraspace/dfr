/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.selenium;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Daniel Bernstein
 *
 */
public class TestSetupWizard extends BaseSeleniumTest {
    private static Logger log = LoggerFactory.getLogger(TestSetupWizard.class);
    @Test
    public void test(){
        sc.open(getAppRoot()+"/setup");
        log.debug("opened setup");
        //welcome screen
        Assert.assertTrue(isTextPresent("Welcome"));
        Assert.assertTrue(isElementPresent("id=next"));
        clickAndWait("id=next");
        log.debug("welcome screen ok");
        //enter credentials screen
        Assert.assertTrue(isTextPresent("Username"));
        sc.type("id=username", System.getProperty("duracloud.username", "admin"));
        sc.type("id=password", System.getProperty("duracloud.password", "apw"));
        sc.type("id=host", System.getProperty("duracloud.host", "danny.duracloud.org"));
        sc.type("id=port", System.getProperty("duracloud.port", "443"));

        log.debug("credentials form filled out");

        clickAndWait("id=next");

        //select space
        Assert.assertTrue(isTextPresent("Space"));
        Assert.assertTrue(isElementPresent("id=spaceId"));

        log.debug("space selector page ready for input");

        String duracloudSpaceId = System.getProperty("dfr.test.spaceId", "dfr-test");
        
        
        sc.select("id=spaceId", "value=" + duracloudSpaceId);
        
        log.debug("attempting to set duracloud space id to " + duracloudSpaceId);
        
        Assert.assertEquals(sc.getValue("id=spaceId"), duracloudSpaceId);
        
        log.debug("spaceId is selected.");
        
        clickAndWait("id=next");

        
        //add a directory
        Assert.assertTrue(isElementPresent("id=directoryPath"));

        //wait for javascript to load: 
        
        sleep(2000);
        
        String tmpDir = System.getProperty("java.io.tmpdir");

        File testDir =
            new File(tmpDir, "dfr-test-dir"
                + String.valueOf(System.currentTimeMillis()));
        testDir.mkdirs();
        
        Assert.assertTrue(testDir.exists());

        
        String[] list = testDir.getAbsolutePath().split(File.separator);
        String path = "/";
        
        for(String dir : list){
            if("".equals(dir)) continue;
            
            path += dir + "/";
             String pathSelector = "css=a[rel='"+path+"']";
             log.debug("checking if " + pathSelector + " is present");
             Assert.assertTrue(isElementPresent(pathSelector));
             sc.click(pathSelector);
             sleep(2000);
        }
        
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
    protected void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
