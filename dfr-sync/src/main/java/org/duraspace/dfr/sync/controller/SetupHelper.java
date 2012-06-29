/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duraspace.dfr.sync.controller;

import org.springframework.stereotype.Component;

/**
 * This class knows about the state of the setup process.
 * 
 * @author Daniel Bernstein
 * 
 */
@Component
public class SetupHelper {
    private static boolean complete = false;

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        SetupHelper.complete = complete;
    }
}
