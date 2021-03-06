/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.sync.domain;

import java.io.Serializable;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.springframework.stereotype.Component;

/**
 * This class backs the Duracloud credentials form.
 * 
 * @author Daniel Bernstein
 * 
 */
@Component("duracloudCredentialsForm")
@GroupSequence({
    DuracloudCredentialsForm.class,
    DuracloudCredentialsForm.HighLevelCoherence.class })
@DuracloudCredentialsCoherenceChecker(groups = DuracloudCredentialsForm.HighLevelCoherence.class)
public class DuracloudCredentialsForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String host;
    private String port;

    public DuracloudCredentialsForm() {}

    public DuracloudCredentialsForm(DuracloudConfiguration config) {
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.host = config.getHost();
        this.port = config.getPort() == 443 ? null : config.getPort()+"";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public interface HighLevelCoherence {
    }

    /**
     * check both basic constraints and high level ones. high level constraints
     * are not checked if basic constraints fail
     */
    @GroupSequence({ Default.class, HighLevelCoherence.class })
    public interface Complete {
    }
}
