/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duraspace.dfr.sync.service;

import java.util.Date;

/**
 * 
 * @author Daniel Bernstein
 */
public class SyncProcessError {
    private Date time;

    private String detail; 
    private String descriptionMessageKey;

    private String suggestedResolutionMessgeKey;

    public Date getTime() {
        return time;
    }

    public String getDescriptionMessageKey() {
        return descriptionMessageKey;
    }

    public String getSuggestedResolutionMessgeKey() {
        return suggestedResolutionMessgeKey;
    }

    public SyncProcessError(String detail){
        this(detail, null,null);
    }

    public SyncProcessError(
        String detail, 
        String descriptionMessageKey, String suggestedResolutionMessgeKey) {
        super();
        this.detail = detail;
        this.time = new Date();
        this.descriptionMessageKey = descriptionMessageKey;
        this.suggestedResolutionMessgeKey = suggestedResolutionMessgeKey;
    }
    
    public String getDetail(){
        return this.detail;
    }

}
