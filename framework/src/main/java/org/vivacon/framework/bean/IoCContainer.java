package org.vivacon.framework.bean;

import org.vivacon.framework.core.ClassScanner;
import org.vivacon.framework.web.Component;
import org.vivacon.framework.web.Controller;
import org.vivacon.framework.web.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IoCContainer {

    private final Map<Class<?>, BeanDefinition> beanDefinitions;

    private final Map<Class<?>, Object> beans;

    public IoCContainer(ClassScanner classScanner, BeanFactory beanFactory) {
        Set<Class<?>> componentAnnotationTypes = new HashSet<>();
        componentAnnotationTypes.add(Component.class);
        componentAnnotationTypes.add(Service.class);
        componentAnnotationTypes.add(Controller.class);

        List<Class<?>> componentClasses = classScanner.scanClassesAnnotatedBy(componentAnnotationTypes);
        for (Class<?> clazz : componentClasses) {
            Object bean = beanFactory.createBean(clazz, Collections.emptyMap());
            beans.put(clazz, bean);
        }
    }

    public Object getBean(Class<?> beanClass) {
        return beans.get(beanClass);
    }

    private void callPostConstructHook(Object bean) {
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
