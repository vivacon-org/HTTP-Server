package org.vivacon.framework.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivacon.framework.bean.annotations.PostConstruct;
import org.vivacon.framework.core.ClassScanner;
import org.vivacon.framework.core.event.ClearCacheEvent;
import org.vivacon.framework.core.event.Event;
import org.vivacon.framework.core.event.EventBroker;
import org.vivacon.framework.core.event.EventListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.gradle.internal.impldep.org.testng.CommandLineArgs.LOG;

public class IoCContainer implements EventListener {
    private final Map<Class<?>, Object> clazzToBean;
    private final Map<String, Set<Object>> bindNameToBeans;
    private final Map<Class<?>, BeanDefinition> beanClazzToDefinition;
    private final ClassScanner classScanner;
    private final MetadataExtractor metadataExtractor;
    private final BeanFactory beanFactory;
    private final BeansInitiationOrderResolver resolver;
    private final Set<Class<? extends Annotation>> managedAnnotations;
    private final Path scanningPath;

    private static final Logger LOG = LoggerFactory.getLogger(IoCContainer.class);

    public IoCContainer(ClassScanner classScanner,
                        MetadataExtractor metadataExtractor,
                        BeanFactory beanFactory,
                        BeansInitiationOrderResolver resolver,
                        Path scanningPath,
                        Set<Class<? extends Annotation>> managedAnnotations) {
        this.clazzToBean = new HashMap<>();
        this.bindNameToBeans = new HashMap<>();
        this.beanClazzToDefinition = new HashMap<>();
        this.classScanner = classScanner;
        this.metadataExtractor = metadataExtractor;
        this.beanFactory = beanFactory;
        this.resolver = resolver;
        this.scanningPath = scanningPath;
        this.managedAnnotations = managedAnnotations;
        EventBroker.getInstance().register(ClearCacheEvent.class, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event instanceof  ClearCacheEvent){
            LOG.info("Clear cache in IoCContainer");
        }
    }

    public Map<Class<?>, Object> loadBeans() {
        List<Class<?>> componentClasses = classScanner.scanClassesAnnotatedBy(scanningPath, managedAnnotations);

        Map<Class<?>, BeanDefinition> beanClazzToDefinition = metadataExtractor.buildBeanDefinitions(componentClasses);
        this.beanClazzToDefinition.putAll(beanClazzToDefinition);

        // initialize beans in order
        List<Class<?>> correctOrderForInitializingBeans = resolver.resolveOrder(beanClazzToDefinition);

        for (Class<?> clazz : correctOrderForInitializingBeans) {

            BeanDefinition beanDefinition = beanClazzToDefinition.get(clazz);
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

        return Collections.unmodifiableMap(clazzToBean);
    }

    public Object getBean(Class<?> beanClass) {

        if (clazzToBean.isEmpty()) {
            Map<Class<?>, Object> loadedBeans = loadBeans();
            this.clazzToBean.putAll(loadedBeans);
        }

        return clazzToBean.get(beanClass);
    }

    public Set<Object> getBeans(String bindName) {
        if (clazzToBean.isEmpty()) {
            Map<Class<?>, Object> loadedBeans = loadBeans();
            this.clazzToBean.putAll(loadedBeans);
        }

        return Collections.unmodifiableSet(bindNameToBeans.get(bindName));
    }

    public Map<Class<?>, BeanDefinition> getBeanClazzToDefinition() {
        return beanClazzToDefinition;
    }

    private void invokePostConstructHook(Object bean) {
        try {
            Class<?> clazz = bean.getClass();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    method.invoke(bean);
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            // no op
        }
    }
}
