/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.setup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * @author Daniel Bernstein
 *
 */
@Target({ElementType.TYPE}) 
@Retention(RetentionPolicy.RUNTIME) 
@Constraint(validatedBy=DuracloudCredentialsCoherenceCheckerValidator.class)
public @interface DuracloudCredentialsCoherenceChecker {
    String message() default "A DuraCloud instance matching your host, username, and password could not be found. " +
    		"Please try reentering your credentials.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
