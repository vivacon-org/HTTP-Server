package org.vivacon.framework.core.event;

import java.util.Map;
import java.util.Set;

public class ClearCacheEvent implements EventListener {
    private final Map<Class<?>, Set<String>> beanClassToBindingNamesCache;

    public ClearCacheEvent(Map<Class<?>, Set<String>> beanClassToBindingNamesCache) {
        this.beanClassToBindingNamesCache = beanClassToBindingNamesCache;
    }


    @Override
    public void handleEvent(Event event) {
        if (event instanceof ClearCacheEvent) {
            beanClassToBindingNamesCache.clear();
        }
    }
}
