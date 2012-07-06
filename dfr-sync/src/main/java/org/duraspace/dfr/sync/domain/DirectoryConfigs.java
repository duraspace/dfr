/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.domain;

import java.util.LinkedHashSet;

import org.springframework.stereotype.Component;


/**
 * A list of configured directories.
 * @author Daniel Bernstein
 *
 */
@Component("directoryConfigs")
public class DirectoryConfigs extends LinkedHashSet<DirectoryConfig>{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    

}
