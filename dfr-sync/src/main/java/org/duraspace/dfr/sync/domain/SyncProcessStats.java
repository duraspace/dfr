/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duraspace.dfr.sync.domain;

import java.util.Date;

/**
 * A Read-only data object deCribing the state of the sync tool from the user's
 * perspective.
 * 
 * @author Daniel Bernstein
 * 
 */

public class SyncProcessStats {
    private SyncProcessState state;
    private Date startDate;
    private Date estimatedCompletionDate;
    private int errorCount;
    private long currentUpBytesPerSecond;
    private long averageUpBytesPerSecond;

    public SyncProcessStats() {
        this.state = SyncProcessState.STOPPED;
        this.startDate = new Date();
        this.estimatedCompletionDate = new Date();
        this.errorCount = 0;
        this.currentUpBytesPerSecond = 2 * 1000 * 1000;
        this.averageUpBytesPerSecond = 2 * 1000 * 1000;
    }

    public SyncProcessStats(
        SyncProcessState state, Date startDate, Date estimatedCompletionDate,
        int errorCount, long currentUpBytesPerSecond,
        long averageUpBytesPerSecond) {
        this.state = state;
        this.startDate = startDate;
        this.estimatedCompletionDate = estimatedCompletionDate;
        this.errorCount = errorCount;
        this.currentUpBytesPerSecond = currentUpBytesPerSecond;
        this.averageUpBytesPerSecond = averageUpBytesPerSecond;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public long getCurrentUpBytesPerSecond() {
        return currentUpBytesPerSecond;
    }

    public long getAverageUpBytesPerSecond() {
        return averageUpBytesPerSecond;
    }

    public SyncProcessState getState() {
        return state;
    }

}
