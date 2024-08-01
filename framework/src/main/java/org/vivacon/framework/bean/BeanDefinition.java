package org.vivacon.framework.bean;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Set;

public class BeanDefinition {
    private final Class<?> beanClass;
    private final Set<String> bindNames;
    private final Constructor<?> injectedConstructor;
    private final LinkedHashMap<Class<?>, Set<String>> dependenciesToBindingNames;

    public BeanDefinition(Class<?> beanClass,
                          Set<String> bindNames,
                          Constructor<?> injectedConstructor,
                          LinkedHashMap<Class<?>, Set<String>> dependenciesToBindingNames) {
        this.bindNames = bindNames;
        this.beanClass = beanClass;
        this.injectedConstructor = injectedConstructor;
        this.dependenciesToBindingNames = dependenciesToBindingNames;
    }

    public Set<String> getBindNames() {
        return bindNames;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Constructor<?> getInjectedConstructor() {
        return injectedConstructor;
    }

    public LinkedHashMap<Class<?>, Set<String>> getDependenciesToBindingNames() {
        return dependenciesToBindingNames;
    }
}
