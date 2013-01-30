/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.sync.util;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class FileSizeFormatter {
    public String format(long fileSize){
        return FormatUtil.readableFileSize(fileSize);
    }
}
