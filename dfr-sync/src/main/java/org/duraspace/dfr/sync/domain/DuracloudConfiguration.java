/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.domain;
/**
 * This class provides read-only Duracloud Configuration info: that is, everything
 * one needs to know in order to connect to a duracloud primary content store.
 * @author Daniel Bernstein
 *
 */
public class DuracloudConfiguration {
    
    private String username;
    private String password;
    private String host;
    private int port;
    private String spaceId;

    public DuracloudConfiguration(
        String username, String password, String host, int port, String spaceId) {
        super();
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.spaceId = spaceId;
    }

    public String getUsername() {
        return username;
    }
    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }
    public String getSpaceId() {
        return spaceId;
    }
    public String getPassword() {
        return password;
    }

}
