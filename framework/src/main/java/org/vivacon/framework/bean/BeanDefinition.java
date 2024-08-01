package org.vivacon.framework.bean;

import java.util.LinkedHashMap;
import java.util.Set;

public class BeanDefinition {
    private final Class<?> beanClass;

    private final Set<String> bindNames;
    private final LinkedHashMap<Class<?>, Set<String>> dependenciesToBindingNames;

    public BeanDefinition(Class<?> beanClass,
                          Set<String> bindNames,
                          LinkedHashMap<Class<?>, Set<String>> dependenciesToBindingNames) {
        this.bindNames = bindNames;
        this.beanClass = beanClass;
        this.dependenciesToBindingNames = dependenciesToBindingNames;
    }

    public Set<String> getBindNames() {
        return bindNames;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public LinkedHashMap<Class<?>, Set<String>> getDependenciesToBindingNames() {
        return dependenciesToBindingNames;
    }
}
