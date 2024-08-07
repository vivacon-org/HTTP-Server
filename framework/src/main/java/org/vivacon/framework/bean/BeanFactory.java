package org.vivacon.framework.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BeanFactory {
    private static final Logger LOG = LoggerFactory.getLogger(BeanFactory.class);

    private static final BeanFactory INSTANCE = new BeanFactory();

    private BeanFactory() {
        // Private constructor to prevent instantiation
    }

    public static BeanFactory getInstance() {
        return INSTANCE;
    }

    public Object createBean(BeanDefinition beanDefinition,
                             Map<Class<?>, Object> clazzToBean,
                             Map<String, Set<Object>> bindNameToBeans) {

        if (clazzToBean.get(beanDefinition.getBeanClass()) != null) {
            return clazzToBean.get(beanDefinition.getBeanClass());
        }

        Constructor<?> injectedConstructor = beanDefinition.getInjectedConstructor();

        try {
            if (injectedConstructor.getParameterCount() > 0) {
                Object[] dependencies = populateDependencies(beanDefinition.getFieldToBindingNames(), bindNameToBeans);
                return injectedConstructor.newInstance(dependencies);
            }


            Object bean = injectedConstructor.newInstance();
            Object[] dependencies = populateDependencies(beanDefinition.getFieldToBindingNames(), bindNameToBeans);
            Field[] declaredFields = beanDefinition.getBeanClass().getDeclaredFields();

            int runner = 0;
            for (Field field : declaredFields) {
                Object injectedDependency = dependencies[runner];
                field.setAccessible(true);
                field.set(bean, injectedDependency);
                runner++;
            }

            return bean;

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(String.format("Can not create bean for %s", beanDefinition), e);
        }
    }

    private Object[] populateDependencies(LinkedHashMap<Field, Set<String>> dependencyToItsBindNames,
                                          Map<String, Set<Object>> bindNameToBeans) {

        Object[] dependencies = new Object[dependencyToItsBindNames.size()];

        int runner = 0;

        for (Map.Entry<Field, Set<String>> entry : dependencyToItsBindNames.entrySet()) {

            Set<String> dependencyBindingNames = entry.getValue();

            Optional<Object> bean = findBeansViaBindingNames(dependencyBindingNames, bindNameToBeans);

            if (bean.isPresent()) {

                dependencies[runner++] = bean.get();
                continue;
            }

            runner++;
        }

        return dependencies;
    }

    protected Optional<Object> findBeansViaBindingNames(Set<String> bindingNames,
                                                      Map<String, Set<Object>> bindNameToBeans) {

        for (String bindingName : bindingNames) {

            Set<Object> beans = bindNameToBeans.get(bindingName);

            if (beans.isEmpty()) {
                return Optional.empty();
            }

            if (beans.size() == 1) {
                return Optional.of(beans.iterator().next());
            }

            LOG.debug("Binding name {} is currently binding to many objects {}", bindingName, beans);
        }

        throw new IllegalArgumentException("Can not find the suitable bean for the binding name, please check the bean registration");
    }
}