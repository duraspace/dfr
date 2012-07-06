package org.duraspace.dfr.sync.service;

import org.duraspace.dfr.sync.domain.SyncProcessState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SyncProcessStateTransitionValidatorTest {
    private SyncProcessStateTransitionValidator v =
        new SyncProcessStateTransitionValidator();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testValidate() {
        validate(SyncProcessState.STOPPED, SyncProcessState.STARTING);
        validate(SyncProcessState.STARTING, SyncProcessState.RUNNING);
        validate(SyncProcessState.RUNNING, SyncProcessState.STOPPING);
        validate(SyncProcessState.STOPPING, SyncProcessState.STOPPED);

        validate(SyncProcessState.STOPPED, SyncProcessState.RUNNING, false);
        validate(SyncProcessState.STOPPED, SyncProcessState.STOPPING, false);

        validate(SyncProcessState.STARTING, SyncProcessState.STOPPED, false);
        validate(SyncProcessState.STARTING, SyncProcessState.STOPPING, false);

        validate(SyncProcessState.RUNNING, SyncProcessState.STOPPED, false);
        validate(SyncProcessState.RUNNING, SyncProcessState.STARTING, false);

        validate(SyncProcessState.STOPPING, SyncProcessState.STARTING, false);
        validate(SyncProcessState.STOPPING, SyncProcessState.RUNNING, false);

        validate(SyncProcessState.STOPPED, SyncProcessState.ERROR, false);
        validate(SyncProcessState.STARTING, SyncProcessState.ERROR);
        validate(SyncProcessState.RUNNING, SyncProcessState.ERROR);
        validate(SyncProcessState.STOPPING, SyncProcessState.ERROR);

    }

    private void validate(SyncProcessState from, SyncProcessState to) {
        validate(from, to, true);
    }

    private void validate(SyncProcessState from,
                          SyncProcessState to,
                          boolean expectedResult) {
        Assert.assertEquals(expectedResult,
                            v.validate(from,
                                                                         to));
    }

}
