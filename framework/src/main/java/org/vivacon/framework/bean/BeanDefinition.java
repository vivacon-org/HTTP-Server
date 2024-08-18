package org.vivacon.framework.bean;

import org.vivacon.framework.core.event.Event;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class BeanDefinition {
    private final Class<?> beanClass;
    private final Set<String> bindNames;
    private final Constructor<?> injectedConstructor;
    private final LinkedHashMap<Parameter, Set<String>> parameterToBindingNames;
    private final LinkedHashMap<Field, Set<String>> fieldToBindingNames;

    //TODO: adding final
    private Map<Class< ? extends Event>, Method> eventToHandler;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Set<String> getBindNames() {
        return bindNames;
    }

    public Constructor<?> getInjectedConstructor() {
        return injectedConstructor;
    }

    public LinkedHashMap<Field, Set<String>> getFieldToBindingNames() {
        return fieldToBindingNames;
    }

    public Map<Class<? extends Event>, Method> getEventToHandler() {
        return eventToHandler;
    }

    private BeanDefinition(BeanDefinitionBuilder beanDefinitionBuilder) {
        this.beanClass = beanDefinitionBuilder.beanClass;
        this.bindNames = beanDefinitionBuilder.bindNames;
        this.injectedConstructor = beanDefinitionBuilder.injectedConstructor;
        this.parameterToBindingNames = beanDefinitionBuilder.parameterToBindingNames;
        this.fieldToBindingNames = beanDefinitionBuilder.fieldToBindingNames;
    }


    public static class BeanDefinitionBuilder {
        private Class<?> beanClass;
        private Set<String> bindNames;
        private Constructor<?> injectedConstructor;
        private LinkedHashMap<Parameter, Set<String>> parameterToBindingNames;
        private LinkedHashMap<Field, Set<String>> fieldToBindingNames;

        public BeanDefinitionBuilder setBeanClass(Class<?> beanClass) {
            this.beanClass = beanClass;
            return this;
        }

        public BeanDefinitionBuilder setBindNames(Set<String> bindNames) {
            this.bindNames = bindNames;
            return this;
        }

        public BeanDefinitionBuilder setInjectedConstructor(Constructor<?> injectedConstructor) {
            this.injectedConstructor = injectedConstructor;
            return this;
        }

        public BeanDefinitionBuilder setParameterToBindingNames(LinkedHashMap<Parameter, Set<String>> parameterToBindingNames) {
            this.parameterToBindingNames = parameterToBindingNames;
            return this;
        }

        public BeanDefinitionBuilder setFieldToBindingNames(LinkedHashMap<Field, Set<String>> fieldToBindingNames) {
            this.fieldToBindingNames = fieldToBindingNames;
            return this;
        }
        public BeanDefinition build() {
            return new BeanDefinition(this);
        }
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanClass=" + beanClass +
                ", bindNames=" + bindNames +
                ", injectedConstructor=" + injectedConstructor +
                ", parameterToBindingNames" + parameterToBindingNames +
                ", fieldToBindingNames" + fieldToBindingNames +
                '}';
    }
}
