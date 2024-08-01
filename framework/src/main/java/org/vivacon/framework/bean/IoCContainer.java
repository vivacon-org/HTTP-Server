package org.vivacon.framework.bean;

import org.vivacon.framework.core.ClassScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class IoCContainer {
    private final Map<Class<?>, Object> clazzToBean;
    private final Map<String, Set<Object>> bindNameToBeans;

    public IoCContainer(ClassScanner classScanner,
                        MetadataExtractor metadataExtractor,
                        BeanFactory beanFactory,
                        Set<Class<? extends Annotation>> managedAnnotations) {
        clazzToBean = new HashMap<>();
        bindNameToBeans = new HashMap<>();

        List<Class<?>> componentClasses = classScanner.scanClassesAnnotatedBy(managedAnnotations);

        Map<Class<?>, BeanDefinition> beanDefinitionMap = metadataExtractor.buildBeanDefinitions(componentClasses);

        // initialize beans in order
        List<Class<?>> correctOrderForInitializingBeans = findCorrectOrderForInitializingBeans(beanDefinitionMap);

        for (Class<?> clazz : correctOrderForInitializingBeans) {

            BeanDefinition beanDefinition = beanDefinitionMap.get(clazz);
            Object bean = beanFactory.createBean(beanDefinition,
                    Collections.unmodifiableMap(clazzToBean),
                    Collections.unmodifiableMap(bindNameToBeans));
            invokePostConstructHook(bean);
            clazzToBean.put(clazz, bean);

            for (String bindName : beanDefinition.getBindNames()) {

                Set<Object> bindingBeans = bindNameToBeans.get(bindName);

                if (bindingBeans == null) {
                    Set<Object> newBindingBeans = new HashSet<>();
                    newBindingBeans.add(bean);
                    bindNameToBeans.put(bindName, newBindingBeans);
                    continue;
                }

                bindingBeans.add(bean);
                bindNameToBeans.put(bindName, bindingBeans);
            }
        }
    }

    /**
     * Perform a simple topological sort to find beans that has no dependencies first, then go to each edge of this vertex
     * to collect more beans until complete the Directed Acyclic Graph (DAG) which represent for the beans dependencies
     *
     * @return
     */
    private List<Class<?>> findCorrectOrderForInitializingBeans(Map<Class<?>, BeanDefinition> beanDefinitions) {
        Map<Class<?>, Integer> inDegree = new HashMap<>();
        Map<Class<?>, List<Class<?>>> graph = new HashMap<>();
        Queue<Class<?>> queue = new LinkedList<>();
        List<Class<?>> sortedList = new ArrayList<>();
        Set<Class<?>> visited = new HashSet<>();
        Set<Class<?>> recursionStack = new HashSet<>();

        // Initialize inDegree and graph
        for (Map.Entry<Class<?>, BeanDefinition> entry : beanDefinitions.entrySet()) {
            Class<?> beanClass = entry.getKey();
            inDegree.put(beanClass, 0);
            graph.put(beanClass, new ArrayList<>());
        }

        // Build graph and calculate in-degrees
        for (Map.Entry<Class<?>, BeanDefinition> entry : beanDefinitions.entrySet()) {
            Class<?> beanClass = entry.getKey();
            BeanDefinition definition = entry.getValue();
            for (Class<?> dependency : definition.getDependenciesToBindingNames().keySet()) {
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
            visited.add(node);
            recursionStack.remove(node); // Node is not in the current path anymore
            for (Class<?> neighbor : graph.get(node)) {
                if (recursionStack.contains(neighbor)) {
                    throw new RuntimeException(String.format("Circular dependency detected involving: %s", neighbor.getName()));
                }
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                    recursionStack.add(neighbor); // Add neighbor to recursion stack
                }
            }
        }

        // Check for remaining nodes with non-zero in-degrees
        for (Map.Entry<Class<?>, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() > 0) {
                throw new RuntimeException("Circular dependency detected");
            }
        }

        return sortedList;
    }

    public Object getBean(Class<?> beanClass) {
        return clazzToBean.get(beanClass);
    }

    private void invokePostConstructHook(Object bean) {
        try {
            Method method = bean.getClass().getMethod("init");

            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(bean);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // no op
        }
    }
}
