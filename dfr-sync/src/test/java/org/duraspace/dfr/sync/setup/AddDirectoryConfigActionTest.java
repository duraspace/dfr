/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import org.duraspace.dfr.test.AbstractTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class AddDirectoryConfigActionTest  extends AbstractTest {
    
    @Test
    public void testExecute() throws Exception {
        AddDirectoryConfigAction action = new AddDirectoryConfigAction();
        RequestContext requestContext = createMock(RequestContext.class);
        
        replay();
        
        action.execute(requestContext);
        Event result = action.execute(requestContext);
        Assert.assertEquals("success", result.getId());

    }

}
