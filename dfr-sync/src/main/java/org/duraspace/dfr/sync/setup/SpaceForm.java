/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import java.io.Serializable;

import org.springframework.stereotype.Component;

/**
 * This class backs the space selection form.
 * @author Daniel Bernstein
 *
 */
@Component("spaceForm")
public class SpaceForm implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String spaceId;

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }
}
