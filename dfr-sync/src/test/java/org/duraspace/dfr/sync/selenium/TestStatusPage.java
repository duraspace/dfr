/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.selenium;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.activemq.util.ByteArrayOutputStream;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.dfr.sync.controller.InitController;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class TestStatusPage extends BasePostSetupPage {

    @Test
    public void testGet() {
        sc.open(getAppRoot() + "/status");
        Assert.assertTrue(isElementPresent("id=active-syncs"));
        Assert.assertTrue(isElementPresent("id=queued"));
        Assert.assertFalse(isElementPresent("id=errors"));
        clickAndWait("css=#errors-tab a");
        Assert.assertTrue(isElementPresent("id=errors"));
        Assert.assertFalse(isElementPresent("id=queued"));
        
    }

    @Test
    public void testGetErrorsTabActivated() {
        sc.open(getAppRoot() + "/status?statusTab=errors");
        Assert.assertTrue(isElementPresent("id=errors"));

    }

}
