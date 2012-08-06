/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class DuracloudCredentialsCoherenceCheckerValidator
    implements
    ConstraintValidator<DuracloudCredentialsCoherenceChecker, DuracloudCredentialsForm> {

    private static Logger log =
        LoggerFactory.getLogger(DuracloudCredentialsCoherenceCheckerValidator.class);

    private ContentStoreFactory contentStoreFactory;

    @Autowired
    public DuracloudCredentialsCoherenceCheckerValidator(
        ContentStoreFactory contentStoreFactory) {
        this.contentStoreFactory = contentStoreFactory;
    }

    @Override
    public void
        initialize(DuracloudCredentialsCoherenceChecker constraintAnnotation) {
    }

    @Override
    public boolean isValid(DuracloudCredentialsForm value,
                           ConstraintValidatorContext context) {
        try {
            ContentStore cs = contentStoreFactory.create(value);
            return cs != null;
        } catch (ContentStoreException ex) {
            log.warn("invalid credentials: " + ex.getMessage(), ex);
            return false;
        }
    }

}
