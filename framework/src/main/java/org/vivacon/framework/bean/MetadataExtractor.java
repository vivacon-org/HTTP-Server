package org.vivacon.framework.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivacon.framework.bean.annotations.Autowired;
import org.vivacon.framework.bean.annotations.Qualifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MetadataExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataExtractor.class);

    private final Map<Class<?>, Set<String>> beanClassToBindingNamesCache;

    private MetadataExtractor() {
        beanClassToBindingNamesCache = new HashMap<>();
        // Private constructor to prevent instantiation
    }

    public void setBeanClassToBindingNamesCache(Map<Class<?>, Set<String>> cache) {
        beanClassToBindingNamesCache.clear();
        beanClassToBindingNamesCache.putAll(cache);
    }
    private static final MetadataExtractor INSTANCE = new MetadataExtractor();

    public static MetadataExtractor getInstance() {
        return INSTANCE;
    }

    public Map<Class<?>, BeanDefinition> buildBeanDefinitions(List<Class<?>> componentClasses) {
        Map<Class<?>, BeanDefinition> classToBeanDefinition = new HashMap<>();

        for (Class<?> clazz : componentClasses) {
            Set<String> beanBindingNames = getBeanBindingName(clazz);
            Constructor<?> constructorToInject = getConstructorToInject(clazz);
            LinkedHashMap<Parameter, Set<String>> parameterToItsBindNames = getDependencyToItsBindNames(constructorToInject);

            if (!parameterToItsBindNames.isEmpty()) {
                new BeanDefinition.BeanDefinitionBuilder()
                        .setBeanClass(clazz)
                        .setBindNames(beanBindingNames)
                        .setInjectedConstructor(constructorToInject)
                        .setParameterToBindingNames(parameterToItsBindNames).build();
            }
            LinkedHashMap<Field, Set<String>> fieldToItsBindNames = getDependencyToItsBindNames(clazz);
            classToBeanDefinition.put(clazz, new BeanDefinition.BeanDefinitionBuilder()
                    .setBeanClass(clazz)
                    .setBindNames(beanBindingNames)
                    .setInjectedConstructor(constructorToInject)
                    .setFieldToBindingNames(fieldToItsBindNames).build());
        }
        return classToBeanDefinition;
    }

    public LinkedHashMap<Parameter, Set<String>> getDependencyToItsBindNames(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        LinkedHashMap<Parameter, Set<String>> dependencyToItsBindNames = new LinkedHashMap<>();

        for (Parameter parameter : parameters) {
            Class<?> dependencyType = parameter.getType();

            Set<String> qualifiedNames = Arrays.stream(parameter.getAnnotationsByType(Qualifier.class)).map(Qualifier::name).collect(Collectors.toSet());
            if (!qualifiedNames.isEmpty()) {
                dependencyToItsBindNames.put(parameter, qualifiedNames);
                continue;
            }

            Set<String> dependencyBindNames = getBeanBindingName(dependencyType);
            dependencyToItsBindNames.put(parameter, dependencyBindNames);
        }

        return dependencyToItsBindNames;
    }


    public LinkedHashMap<Field, Set<String>> getDependencyToItsBindNames(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        LinkedHashMap<Field, Set<String>> dependencyToItsBindNames = new LinkedHashMap<>();

        for (Field field : declaredFields) {
            Class<?> dependencyType = field.getType();

            Set<String> qualifiedNames = Arrays.stream(field.getAnnotationsByType(Qualifier.class)).map(Qualifier::name).collect(Collectors.toSet());
            if (!qualifiedNames.isEmpty()) {
                dependencyToItsBindNames.put(field, qualifiedNames);
                continue;
            }

            Set<String> dependencyBindNames = getBeanBindingName(dependencyType);
            dependencyToItsBindNames.put(field, dependencyBindNames);
        }

        return dependencyToItsBindNames;
    }

    public Set<String> getBeanBindingName(Class<?> beanClazz) {
        if (beanClassToBindingNamesCache.get(beanClazz) != null) {
            return beanClassToBindingNamesCache.get(beanClazz);
        }

        Set<String> bindingNames = Arrays.stream(beanClazz.getInterfaces()).map(Class::getSimpleName).collect(Collectors.toSet());
        bindingNames.add(beanClazz.getSimpleName());

        Qualifier[] qualifiers = beanClazz.getAnnotationsByType(Qualifier.class);
        if (qualifiers.length == 0) {
            beanClassToBindingNamesCache.put(beanClazz, bindingNames);
            return bindingNames;
        }

        for (Qualifier qualifier : qualifiers) {
            String qualifiedName = qualifier.name();
            bindingNames.add(qualifiedName);
        }

        beanClassToBindingNamesCache.put(beanClazz, bindingNames);
        return bindingNames;
    }

    public Constructor<?> getConstructorToInject(Class<?> beanClazz) {
        Constructor<?> defeaultConstructor = null;
        try {
            defeaultConstructor = beanClazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            // just ignore
        }

        // only get the public constructors
        Constructor<?>[] constructors = beanClazz.getDeclaredConstructors();

        if (constructors.length == 1 && defeaultConstructor != null) {
            return defeaultConstructor;
        }

        if (constructors.length == 1 && constructors[0].getParameters().length > 0) {
            return constructors[0];
        }

        for (Constructor<?> constructor : constructors) {
            Autowired[] attachedAutowiredAnnotations = constructor.getAnnotationsByType(Autowired.class);
            if (attachedAutowiredAnnotations.length > 0) {
                return constructor;
            }
        }

        throw new IllegalArgumentException(String.format("The class %s has many constructor but no one was annotated by Autowired to specify which the dependencies to inject to the bean", beanClazz));
    }
}
