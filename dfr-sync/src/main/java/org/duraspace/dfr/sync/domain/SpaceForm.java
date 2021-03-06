/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.sync.domain;

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
