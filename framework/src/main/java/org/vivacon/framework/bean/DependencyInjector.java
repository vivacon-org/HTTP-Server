package org.vivacon.framework.bean;

import java.lang.reflect.Field;

public class DependencyInjector {

    private final BeanFactory beanFactory;

    public DependencyInjector(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void injectDependencies() {
        for (Object bean : beanFactory.getBeanContainer().values()) {
            Class<?> clazz = bean.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    Object dependency = getDependency(field);
                    if (dependency != null) {
                        try {
                            field.set(bean, dependency);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Failed to inject dependency: " + field.getName(), e);
                        }
                    }
                }
            }
        }
    }

    private Object getDependency(Field field) {
        Class<?> fieldType = field.getType();
        String beanName = fieldType.getName().toUpperCase();
        if (field.isAnnotationPresent(Qualifier.class)) {
            Qualifier qualifier = field.getAnnotation(Qualifier.class);
            // Logic to find bean by qualifier value
        }
        return beanFactory.getBean(fieldType);
    }
}
