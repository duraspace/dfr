/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.duracloud.sync.config.SyncToolConfig;
import org.duraspace.dfr.sync.domain.DirectoryConfig;
import org.duraspace.dfr.sync.domain.DirectoryConfigs;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
@Component
public class SyncConfigurationManagerImpl implements SyncConfigurationManager {
    private static Logger log =
        LoggerFactory.getLogger(SyncConfigurationManagerImpl.class);
    private SyncToolConfig syncToolConfig;

    public SyncConfigurationManagerImpl() {
        syncToolConfig = deserializeSyncToolConfig();
    }

    private void persistSyncToolConfig() throws IOException {
        SyncToolConfigSerializer.serialize(syncToolConfig,
                                           getSyncToolConfigXmlPath());
    }

    private SyncToolConfig deserializeSyncToolConfig() {
        SyncToolConfig config;
        try {
            config =
                SyncToolConfigSerializer.deserialize(getSyncToolConfigXmlPath());
        } catch (IOException ex) {
            log.warn("unable to deserialize sync config : " + ex.getMessage());
            log.info("creating new config...");
            config = new SyncToolConfig();
            initializeDefaultValues(config);
        }

        return config;
    }

    // FIXME - for testing purposes
    private void initializeDefaultValues(SyncToolConfig config) {
        config.setContext("durastore");
        config.setExitOnCompletion(false);
        config.setSyncDeletes(false);
        List<File> dirs = new ArrayList<File>();
        config.setContentDirs(dirs);
        File workDir =
            new File(System.getProperty("java.io.tmpdir")
                + File.separator + ".dfr-sync-work");
        if (!workDir.mkdirs()) {
            log.info(workDir.getAbsolutePath() + " already exists.");
        }
        config.setWorkDir(workDir);
    }

    private String getSyncToolConfigXmlPath() {
        String configPath =
            System.getProperty("user.home")
                + File.separator + ".dfr-sync-config";

        if (System.getProperty("dfr.config.path") != null) {
            configPath = System.getProperty("dfr.config.path");
        }

        return configPath;
    }

    @Override
    public void persistDuracloudConfiguration(String username,
                                              String password,
                                              String host,
                                              String port,
                                              String spaceId) {
        this.syncToolConfig.setUsername(username);
        this.syncToolConfig.setPassword(password);
        this.syncToolConfig.setHost(host);
        if (!StringUtils.isBlank(port)) {
            this.syncToolConfig.setPort(Integer.parseInt(port));
        } else {
            this.syncToolConfig.setPort(443);
        }

        this.syncToolConfig.setSpaceId(spaceId);

        try {
            persistSyncToolConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public DuracloudConfiguration retrieveDuracloudConfiguration() {
        SyncToolConfig s = this.syncToolConfig;
        return new DuracloudConfiguration(s.getUsername(),
                                          s.getPassword(),
                                          s.getHost(),
                                          s.getPort(),
                                          s.getSpaceId());
    }

    @Override
    public DirectoryConfigs retrieveDirectoryConfigs() {
        DirectoryConfigs c = new DirectoryConfigs();
        List<File> dirs = this.syncToolConfig.getContentDirs();
        for (File f : dirs) {
            c.add(new DirectoryConfig(f.getAbsolutePath()));
        }
        return c;
    }

    @Override
    public void persistDirectoryConfigs(DirectoryConfigs configs) {
        List<File> dirs = new LinkedList<File>();

        for (DirectoryConfig f : configs) {
            dirs.add(new File(f.getDirectoryPath()));
        }
        this.syncToolConfig.setContentDirs(dirs);

        try {
            persistSyncToolConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isConfigurationComplete() {
        SyncToolConfig c = this.syncToolConfig;
        if (c == null) {
            return false;
        }

        if (StringUtils.isBlank(c.getUsername())
            || StringUtils.isBlank(c.getUsername())
            || StringUtils.isBlank(c.getPassword())
            || StringUtils.isBlank(c.getHost())
            || StringUtils.isBlank(c.getSpaceId())
            || CollectionUtils.isEmpty(c.getContentDirs())) {
            return false;

        }

        return true;
    }
}
