package org.vivacon.framework.core.event;

import java.util.ArrayList;
import java.util.List;

public class EventBroker {
    private static EventBroker instance = new EventBroker();
    private List<EventListener> listeners = new ArrayList<>();

    public static EventBroker getInstance() {
        return instance;
    }


    /*add listener in EventBroker*/
    public void subscribe(EventListener listener) {
        if (listener == null) return;
        if (this.listeners == null) this.listeners = new ArrayList<>();
        listeners.add(listener);
    }

    /*publish event - condition event must be subscribe in EventBroker before*/
    public void publish(Event event) {
        if (event == null) return;
        for (EventListener listener: listeners) {
            if (listener == null) continue;
            listener.handleEvent(event);
        }
    }
}
