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

    /**
     * Creates and returns an instance of a bean based on the provided {@link BeanDefinition}.
     *
     * <p>This method performs the following operations:
     * <ul>
     *   <li>Checks if an instance of the bean class already exists in the {@code clazzToBean} map. If it does,
     *   it returns the existing instance.</li>
     *   <li>If the bean has a constructor with parameters, it resolves the dependencies using
     *   {@link #populateDependencies} and uses them to create the bean instance.</li>
     *   <li>If the constructor has no parameters, it creates the bean instance and injects the dependencies
     *   directly into the fields of the bean class.</li>
     *   <li>If the bean creation fails due to reflection errors, a {@link RuntimeException} is thrown.</li>
     * </ul>
     *
     * @param beanDefinition the {@link BeanDefinition} containing metadata about the bean class, constructor,
     *                       and field dependencies.
     * @param clazzToBean a map that holds existing bean instances, keyed by their class type.
     * @param bindNameToBeans a map that holds sets of beans, keyed by binding names, to resolve dependencies.
     * @return the created bean instance.
     * @throws RuntimeException if the bean cannot be created due to reflection issues.
     */
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


    /**
     * Resolves and returns an array of dependencies based on the provided mapping of fields to binding names.
     *
     * <p>This method iterates through the fields and their associated binding names, attempting to find the
     * appropriate bean from the provided {@code bindNameToBeans} map. If a matching bean is found, it is added
     * to the dependencies array. If no matching bean is found, the corresponding array slot remains null.
     *
     * @param dependencyToItsBindNames a {@link LinkedHashMap} where each key is a {@link Field} that requires
     *                                 a dependency, and each value is a {@link Set} of binding names that can
     *                                 be used to resolve the dependency.
     * @param bindNameToBeans a {@link Map} where the keys are binding names and the values are {@link Set}s of
     *                        beans associated with those binding names.
     * @return an array of {@link Object} instances representing the resolved dependencies. The array's order
     *         corresponds to the order of fields in the {@code dependencyToItsBindNames} map.
     *         If a dependency could not be resolved, the corresponding array element will be {@code null}.
     */
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