/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import org.duraspace.dfr.sync.domain.DirectoryConfigForm;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.webflow.execution.Event;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class AddDirectoryConfigActionTest  extends AbstractTest {
    
    @Test
    public void testExecute() throws Exception {
        AddDirectoryConfigAction action = new AddDirectoryConfigAction();
        DirectoryConfigForm form = new DirectoryConfigForm();
        form.setDirectoryPath("test");
        DirectoryConfigs configs = new DirectoryConfigs();
        replay();
        
        action.execute(form, configs);
        Assert.assertEquals(1, configs.size());

    }

}
