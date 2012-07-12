/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.service;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.error.ContentStoreException;

/**
 * 
 * @author Daniel Bernstein
 * 
 */

public interface ContentStoreManagerFactory {

    public ContentStoreManager create() throws ContentStoreException;
}
