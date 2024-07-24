package org.vivacon.framework.bean;

public class BeanDefinition {
    private final String bindName;
    private final Class<?> beanClass;
    private final Object instance;

    public BeanDefinition(String bindName,
                          Class<?> beanClass,
                          Object instance) {
        this.bindName = bindName;
        this.beanClass = beanClass;
        this.instance = instance;
    }

    public String getBindName() {
        return bindName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Object getInstance() {
        return instance;
    }
}
