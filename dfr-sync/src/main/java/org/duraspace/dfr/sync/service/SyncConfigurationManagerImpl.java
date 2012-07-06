/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.duracloud.sync.config.SyncToolConfig;
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

    private SyncToolConfig deserializeSyncToolConfig() {
        SyncToolConfig config;
        try {
            config =
                SyncToolConfigSerializer.deserialize(getSyncToolConfigXmlPath());
        } catch (IOException ex) {
            log.warn("unable to deserialize sync config : "
                + ex.getMessage());
            log.info("creating new config...");
            config = new SyncToolConfig();
            initializeDefaultValues(config);
        }

        return config;
    }

    private void initializeDefaultValues(SyncToolConfig config) {
        config.setContext("durastore");
        config.setExitOnCompletion(false);
        config.setSyncDeletes(false);
        
        config.setUsername("admin");
        config.setPassword("apw");
        config.setPort(8080);
        List<File> dirs = new ArrayList<File>();
        dirs.add(new File("/tmp/dfr-test"));
        config.setContentDirs(dirs);
        config.setSpaceId("dfr-test");
        File workDir = new File(System.getProperty("user.home") + File.separator + ".dfr-sync-work");
        if(!workDir.mkdirs()){
            log.info(workDir.getAbsolutePath() + " already exists.");
        }
        config.setWorkDir(workDir);

    }

    private String getSyncToolConfigXmlPath() {
        return System.getProperty("user.home")
            + File.pathSeparator + ".dfr-sync-config";
    }

    @Override
    public void persistDuracloudConfiguration(String username,
                                              String password,
                                              String host,
                                              int port,
                                              String spaceId) {
        // TODO Auto-generated method stub

    }

    @Override
    public DuracloudConfiguration retrieveDuracloudConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DirectoryConfigs retrieveDirectoryConfigs() {
        // TODO Auto-generated method stub
        return new DirectoryConfigs();
    }

    @Override
    public boolean isConfigurationComplete() {
        // TODO Auto-generated method stub
        return true;
    }

}
