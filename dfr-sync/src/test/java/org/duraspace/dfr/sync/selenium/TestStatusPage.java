package org.duraspace.dfr.sync.selenium;

import org.junit.Test;

public class TestStatusPage extends BaseSeleniumTest {
    @Test
    public void testGet(){
        sc.open(getAppRoot()+"/status");
    }
}
