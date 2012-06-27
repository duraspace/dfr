/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.selenium;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public abstract class BaseSeleniumTest {


    protected static Logger log =
        LoggerFactory.getLogger(BaseSeleniumTest.class);

    protected Selenium sc;
    private static String DEFAULT_PORT = "8888";

    protected String getAppRoot() {
        return "/dfr-sync";
    }

    private String getPort() throws Exception {
        String port = System.getProperty("jetty.port");
        return port != null ? port : DEFAULT_PORT;
    }



    @Before
    public void before() throws Exception {
        String url = "http://localhost:" + getPort() + getAppRoot() + "/";
        sc = createSeleniumClient(url);
        sc.start();
        log.info("started selenium client on " + url);
    }




    @After
    public void after() {
        sc.stop();
        sc = null;
        log.info("stopped selenium client");
    }

    protected boolean isTextPresent(String pattern) {
        return sc.isTextPresent(pattern);
    }

    protected boolean isElementPresent(String locator) {
        return sc.isElementPresent(locator);
    }

    protected Selenium createSeleniumClient(String url) {

        return new DefaultSelenium("localhost", 4444, "*firefox", url);
    }


    /**
     * @param sc
     */
    protected void waitForPage() {
        log.debug("waiting for page to load...");
        sc.waitForPageToLoad(DEFAULT_PAGE_LOAD_WAIT_IN_MS);
        log.debug("body=" + sc.getBodyText());
    }

    protected void clickAndWait(String locator) {
        sc.click(locator);
        log.debug("clicked " + locator);
        waitForPage(sc);
    }

    
    public static String DEFAULT_PAGE_LOAD_WAIT_IN_MS = "30000";
    

    

    /**
     * @param sc
     */
    public static void waitForPage(Selenium sc) {
        log.debug("waiting for page to load...");
        sc.waitForPageToLoad(DEFAULT_PAGE_LOAD_WAIT_IN_MS);
        log.debug("body=" + sc.getBodyText());
    }

    public static void clickAndWait(Selenium sc, String locator) {
        sc.click(locator);
        log.debug("clicked " + locator);
        waitForPage(sc);
    }

}
