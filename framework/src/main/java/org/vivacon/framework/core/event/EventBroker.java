package org.vivacon.framework.core.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

public class EventBroker {
    private static EventBroker instance = new EventBroker();
    public static EventBroker getInstance() {
        return instance;
    }
    private Map<Class<? extends Event>, List<ListenerSource>> topicToListeners = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void register(Class<? extends Event> eventType, EventListener listener) {
        if (listener == null) return;

        ListenerSource source = new ListenerSource(listener);
        topicToListeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(source);
    }

    public void register(Class<? extends Event> eventType, Object listener, Method handlingMethod) {
        if (listener == null) return;

        ListenerSource source = new ListenerSource(listener, handlingMethod);
        topicToListeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(source);
    }

    public List<CompletableFuture> publish(Event event) {
        Class<? extends Event> eventType = event.getClass();
        List<ListenerSource> listenerSources = topicToListeners.get(eventType);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        if (listenerSources == null || listenerSources.isEmpty()) {
            return Collections.emptyList();
        }

        for (ListenerSource listenerSource : listenerSources) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    if(listenerSource.listenerInstance instanceof EventListener) {
                        ((EventListener) listenerSource.listenerInstance).handleEvent(event);
                        return;
                    }

                    listenerSource.listenerMethod.invoke(listenerSource.listenerInstance, event);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("something went wrong with the async handler %s for event $s",
                            listenerSource.listenerInstance, event), e);
                }
            }, executor);
            futures.add(future);
        }
        return Collections.unmodifiableList(futures);
    }

    private static class ListenerSource {

        private Object listenerInstance;

        private Method listenerMethod;

        public ListenerSource(Object listenerInstance) {
            // for internal framework event listeners
            this.listenerInstance = listenerInstance;
        }

        public ListenerSource(Object listenerInstance, Method listenerMethod) {
            // for instances of dev who uses our framework
            this.listenerInstance = listenerInstance;
            this.listenerMethod = listenerMethod;
        }
    }
}
