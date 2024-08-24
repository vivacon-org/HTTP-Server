package org.vivacon.framework.core;

import org.vivacon.framework.bean.*;
import org.vivacon.framework.bean.annotation.Component;
import org.vivacon.framework.bean.annotation.Service;
import org.vivacon.framework.event.Event;
import org.vivacon.framework.event.EventBroker;
import org.vivacon.framework.web.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The entry point for the Vivacon Framework application. This class is responsible for initializing
 * and running the application by scanning for annotated classes, loading beans into the IoC container,
 * and registering event listeners with the {@code EventBroker}.
 */
public class VivaconApplication {
    private Class<?> mainClass;

    public VivaconApplication(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public void run() {
        try {
            Set<Class<? extends Annotation>> managedAnnotations = new HashSet<>();
            managedAnnotations.add(RestController.class);
            managedAnnotations.add(Service.class);
            managedAnnotations.add(Component.class);

            URL location = mainClass.getProtectionDomain().getCodeSource().getLocation();
            Path scanningPath = Path.of(location.toURI());

            IoCContainer ioCContainer = new IoCContainer(ClassScanner.getInstance(), MetadataExtractor.getInstance(), BeanFactory.getInstance(), BeansInitiationOrderResolver.getInstance(), scanningPath, managedAnnotations);
            ioCContainer.loadBeans();


            Map<Class<?>, Object> beansContainer = ioCContainer.loadBeans();
            Map<Class<?>, BeanDefinition> beanDefinitionContainer = ioCContainer.getBeanClazzToDefinition();

            for (Map.Entry<Class<?>, BeanDefinition> eachBeanDefinitionEntry : beanDefinitionContainer.entrySet()) {
                BeanDefinition beanDefinition = eachBeanDefinitionEntry.getValue();
                Object listener = beansContainer.get(beanDefinition.getClass());

                if (listener != null) {
                    for (Method method : beanDefinition.getBeanClass().getDeclaredMethods()) {

                        if (method.getParameterCount() == 1) {
                            Class<?> paramType = method.getParameterTypes()[0];


                            if (Event.class.isAssignableFrom(paramType)) {
                                @SuppressWarnings("unchecked")
                                Class<? extends Event> eventType = (Class<? extends Event>) paramType;
                                method.setAccessible(true);

                                EventBroker.getInstance().register(eventType, listener, method);
                            }
                        }
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
