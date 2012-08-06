/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.error.ContentStoreException;
import org.duraspace.dfr.sync.domain.DuracloudConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
@Component("contentStoreManagerFactory")
public class ContentStoreManagerFactoryImpl
    implements ContentStoreManagerFactory {
    private SyncConfigurationManager syncConfigurationManager;

    @Autowired
    public ContentStoreManagerFactoryImpl(
        @Qualifier("syncConfigurationManager") SyncConfigurationManager syncConfigurationManager) {
        this.syncConfigurationManager = syncConfigurationManager;
    }

    @Override
    public ContentStoreManager create() throws ContentStoreException {
        DuracloudConfiguration dc =
            this.syncConfigurationManager.retrieveDuracloudConfiguration();
        ContentStoreManager csm =
            new ContentStoreManagerImpl(dc.getHost(),
                                        String.valueOf(dc.getPort()));
        return csm;

    }

}
