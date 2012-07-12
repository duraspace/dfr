/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.domain;

import java.io.Serializable;

/**
 * A configured directory.
 * 
 * @author Daniel Bernstein
 * 
 */
public class DirectoryConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String directoryPath;

    public DirectoryConfig(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DirectoryConfig) {
            return ((DirectoryConfig) obj).directoryPath.equals(this.directoryPath);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return directoryPath.hashCode()*13;
    }

}
