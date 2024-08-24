package org.vivacon.server.life_cycle;

public class LifeCycleEvent {

    private final LifeCycle source;

    private final Object data;

    private final String type;

    public LifeCycleEvent(LifeCycle lifecycle, String type, Object data) {
        this.source = lifecycle;
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public LifeCycle getLifecycle() {
        return source;
    }

    public String getType() {
        return this.type;
    }
}
