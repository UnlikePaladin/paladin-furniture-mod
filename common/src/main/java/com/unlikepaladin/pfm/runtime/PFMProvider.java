package com.unlikepaladin.pfm.runtime;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public abstract class PFMProvider {
    private final PFMGenerator parent;
    private final String providerName;
    private final Stopwatch stopwatch;
    public PFMProvider(PFMGenerator parent, String providerName) {
        this.parent = parent;
        this.providerName = providerName;
        this.stopwatch = Stopwatch.createUnstarted();
    }

    protected void startProviderRun() {
        parent.log("Starting provider: {}", providerName);
        this.stopwatch.start();
    }

    protected void endProviderRun() {
        stopwatch.stop();
        String notification = String.format("%s finished after %s ms", providerName, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        parent.log(notification);
        parent.setNotification(notification);
        parent.incrementCount();
    }

    public abstract void run();

    public PFMGenerator getParent() {
        return parent;
    }
}
