/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.util.ArrayList;
import java.util.List;

import org.duracloud.common.constant.Constants;
import org.springframework.stereotype.Component;

/**
 * This class filters out the duracloud system spaces from a list of spaces.
 * @author Daniel Bernstein
 * 
 */
@Component("spacesFilter")
public class SpacesFilter {

    public List<String> filter(List<String> spaces){
        List<String> results = new ArrayList<String>();
        results.addAll(spaces);
        results.removeAll(Constants.SYSTEM_SPACES);
        return results;
    }

}
