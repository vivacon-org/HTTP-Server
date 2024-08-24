package org.vivacon.server.life_cycle;

public enum LifeCycleState {

    NEW(false, null),
    INITIALIZING(false, LifeCycle.BEFORE_INIT_EVENT),
    INITIALIZED(false, LifeCycle.AFTER_INIT_EVENT),
    STARTING_PREP(false, LifeCycle.BEFORE_START_EVENT),
    STARTING(true, LifeCycle.START_EVENT),
    STARTED(true, LifeCycle.AFTER_START_EVENT),
    STOPPING_PREP(true, LifeCycle.BEFORE_STOP_EVENT),
    STOPPING(false, LifeCycle.STOP_EVENT),
    STOPPED(false, LifeCycle.AFTER_STOP_EVENT),
    DESTROYING(false, LifeCycle.BEFORE_DESTROY_EVENT),
    DESTROYED(false, LifeCycle.AFTER_DESTROY_EVENT),
    FAILED(false, null);

    private final boolean available;
    private final String lifecycleEvent;

    LifeCycleState(boolean available, String lifecycleEvent) {
        this.available = available;
        this.lifecycleEvent = lifecycleEvent;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getLifecycleEvent() {
        return lifecycleEvent;
    }
}
