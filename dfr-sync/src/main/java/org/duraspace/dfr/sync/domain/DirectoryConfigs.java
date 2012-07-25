/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.domain;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

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

    public List<File> toFileList() {
        List<File> list = new LinkedList<File>();
        
        for(DirectoryConfig config : this){
            list.add(new File(config.getDirectoryPath()));
        }
        return list;
    }
    

}
