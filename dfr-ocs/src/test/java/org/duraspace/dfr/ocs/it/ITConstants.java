/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duraspace.dfr.ocs.it;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * System property-based constants for use with OCS integration tests.
 */
public final class ITConstants {
    private final static Logger logger =
            LoggerFactory.getLogger(ITConstants.class);

    public final static String DURACLOUD_SCHEME;
    public final static String DURACLOUD_HOSTNAME;
    public final static String DURACLOUD_PORT;
    public final static String DURACLOUD_USERNAME;
    public final static String DURACLOUD_PASSWORD;
    public final static String DURACLOUD_BROKER_PORT;
    public final static String DURACLOUD_BROKER_URL;
    public final static String DURASTORE_CONTEXT;
    public final static String DURASTORE_BASE_URL;
    public final static String FEDORA_BASE_URL;
    public final static String FEDORA_USERNAME;
    public final static String FEDORA_PASSWORD;

    static {
        DURACLOUD_SCHEME = get("duracloud.scheme", "https");
        DURACLOUD_HOSTNAME = get("duracloud.hostname",
                "dfrtest.duracloud.org");
        DURACLOUD_PORT = get("duracloud.port", "443");
        DURACLOUD_USERNAME = get("duracloud.username", "dfrtest");
        DURACLOUD_PASSWORD = get("duracloud.password", null);
        DURACLOUD_BROKER_PORT = get("duracloud.brokerport", "61617");
        DURACLOUD_BROKER_URL = "tcp://" + DURACLOUD_HOSTNAME + ":" +
                DURACLOUD_BROKER_PORT;
        DURASTORE_CONTEXT = get("durastore.context", "durastore");
        String duraStoreBaseURL = DURACLOUD_SCHEME + "://" +
                DURACLOUD_HOSTNAME;
        if (!DURACLOUD_PORT.equals("443")) {
            duraStoreBaseURL += ":" + DURACLOUD_PORT;
        }
        DURASTORE_BASE_URL = duraStoreBaseURL + "/" + DURASTORE_CONTEXT;
        FEDORA_BASE_URL = get("fedora.baseurl",
                "http://dfr.duracloud.org:8080/fedora");
        FEDORA_USERNAME = get("fedora.username", "fedoraAdmin");
        FEDORA_PASSWORD = get("fedora.password", null);
    }

    private ITConstants() { }

    private static String get(String name, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null) {
            if (defaultValue == null) {
                String message = "Required system property undefined: " + name;
                logger.error(message);
                System.out.println("ERROR: " + message);
                throw new RuntimeException(message);
            }
            return defaultValue;
        }
        return value;
    }
}
