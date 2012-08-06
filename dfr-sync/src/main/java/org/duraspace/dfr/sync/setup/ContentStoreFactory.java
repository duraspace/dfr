/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
@Component("contentStoreFactory")
public class ContentStoreFactory {

    private static Logger log =
        LoggerFactory.getLogger(ContentStoreFactory.class);

    public ContentStore create(DuracloudCredentialsForm dcf)
        throws ContentStoreException {
        String username = dcf.getUsername();
        String host = dcf.getHost();
        String port = dcf.getPort();
        ContentStoreManager csm = createContentStoreManager(host, port);

        Credential credential = new Credential(username, dcf.getPassword());
        csm.login(credential);
        log.debug("logged into {}:{} as {}", new Object[] {
            host, port, username });
        ContentStore primary = csm.getPrimaryContentStore();
        log.debug("retrieved primary content store");
        return primary;
    }

    protected ContentStoreManager createContentStoreManager(String host,
                                                            String port) {
        ContentStoreManager csm = new ContentStoreManagerImpl(host, port);
        return csm;
    }
}
