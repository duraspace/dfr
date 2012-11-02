package org.duraspace.dfr.sync.util;


public class FileSizeFormatter {
    public String format(long fileSize){
        return FormatUtil.readableFileSize(fileSize);
    }
}
