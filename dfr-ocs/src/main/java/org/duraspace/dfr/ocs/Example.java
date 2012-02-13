package org.duraspace.dfr.ocs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An example class.
 */
public class Example {
    private static final Logger logger = LoggerFactory.getLogger(Example.class);

    private final String value;

    /**
     * Creates an instance.
     */
    public Example(String value) {
        this.value = value;
        logger.info("Constructed Example");
    }

    /**
     * Gets the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        throw new UnsupportedOperationException();
    }

}
