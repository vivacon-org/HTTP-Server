package org.vivacon.framework.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The {@code EventBroker} class is responsible for managing event listeners and publishing events
 * asynchronously. It acts as a mediator between event publishers and subscribers, ensuring that
 * events are handled by the appropriate listeners.
 * <p>
 * The {@code EventBroker} is implemented as a singleton, ensuring only one instance exists
 * throughout the application lifecycle.
 */
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

        // Filter out ListenerSources with null listenerInstances
        listenerSources.removeIf(listenerSource -> listenerSource.listenerInstance.get() == null);

        for (ListenerSource listenerSource : listenerSources) {

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Object listenerInstance = listenerSource.listenerInstance.get();
                    if (listenerInstance == null) return;

                    if (listenerInstance instanceof EventListener) {
                        ((EventListener) listenerInstance).handleEvent(event);
                        return;
                    }
                    Method method = listenerSource.listenerMethod.get();
                    if (method == null) {
                        return;
                    }
                    method.invoke(listenerInstance, event);
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

        private WeakReference<Object> listenerInstance;

        private WeakReference<Method> listenerMethod;

        public ListenerSource(Object listenerInstance) {
            // for internal framework event listeners
            this.listenerInstance = new WeakReference<Object>(listenerInstance);
        }

        public ListenerSource(Object listenerInstance, Method listenerMethod) {
            // for instances of dev who uses our framework
            this.listenerInstance = new WeakReference<>(listenerInstance);;
            this.listenerMethod = new WeakReference<>(listenerMethod);
        }
    }
}
