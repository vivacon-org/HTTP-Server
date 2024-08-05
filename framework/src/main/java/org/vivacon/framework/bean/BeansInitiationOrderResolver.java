package org.vivacon.framework.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BeansInitiationOrderResolver {

    private static final BeansInitiationOrderResolver instance = new BeansInitiationOrderResolver();

    private BeansInitiationOrderResolver(){}

    public static BeansInitiationOrderResolver getInstance(){
        return instance;
    }

    public List<Class<?>> resolveOrder(Map<Class<?>, BeanDefinition> beanDefinitions) {
        Map<Class<?>, Integer> inDegree = new LinkedHashMap<>();
        Map<Class<?>, List<Class<?>>> graph = new HashMap<>();
        Queue<Class<?>> queue = new LinkedList<>();
        List<Class<?>> sortedList = new ArrayList<>();

        // Initialize inDegree and graph
        for (Class<?> beanClass : beanDefinitions.keySet()) {
            inDegree.put(beanClass, 0);
            graph.put(beanClass, new ArrayList<>());
        }

        // Build graph and calculate in-degrees
        for (Map.Entry<Class<?>, BeanDefinition> entry : beanDefinitions.entrySet()) {
            Class<?> beanClass = entry.getKey();
            BeanDefinition definition = entry.getValue();

            for (Class<?> dependency : definition.getDependenciesToBindingNames().keySet()) {
                if (graph.get(dependency) == null){
                    continue;
                }
                graph.get(dependency).add(beanClass);
                inDegree.put(beanClass, inDegree.get(beanClass) + 1);
            }
        }
        // Find nodes with zero in-degree
        for (Map.Entry<Class<?>, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }
        // Perform topological sort
        while (!queue.isEmpty()) {
            Class<?> node = queue.poll();
            sortedList.add(node);
            for (Class<?> neighbor : graph.get(node)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }
        // Check for remaining nodes with non-zero in-degrees
        for (Integer degree : inDegree.values()) {
            if (degree > 0) {
                throw new RuntimeException(String.format("Circular dependency detected among %s", inDegree.keySet()));
            }
        }
        return sortedList;
    }
}