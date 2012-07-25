/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.config;

import org.springframework.core.convert.converter.Converter;

public class StringTrimmerConverter implements Converter<String,String> {

    @Override
    public String convert(String source) {
        if(source == null){
            return null;
        }
        
        String trimmed = ((String)source).trim();
        if(trimmed.length() == 0){
            return null;
        }else{
            return trimmed;
        }
    }
}
