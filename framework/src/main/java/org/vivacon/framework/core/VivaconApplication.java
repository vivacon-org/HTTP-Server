package org.vivacon.framework.core;

import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated;
import org.vivacon.framework.bean.*;
import org.vivacon.framework.bean.annotations.Component;
import org.vivacon.framework.bean.annotations.Service;
import org.vivacon.framework.core.event.Event;
import org.vivacon.framework.core.event.EventBroker;
import org.vivacon.framework.web.annotations.Controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VivaconApplication {
    private Class<?> mainClass;

    public VivaconApplication(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public void run() {

        try {
            Set<Class<? extends Annotation>> managedAnnotations = new HashSet<>();
            managedAnnotations.add(Controller.class);
            managedAnnotations.add(Service.class);
            managedAnnotations.add(Component.class);

            URL location = mainClass.getProtectionDomain().getCodeSource().getLocation();
            Path scanningPath = Path.of(location.toURI());

            IoCContainer ioCContainer = new IoCContainer(ClassScanner.getInstance(), MetadataExtractor.getInstance(), BeanFactory.getInstance(), BeansInitiationOrderResolver.getInstance(), scanningPath, managedAnnotations);
            ioCContainer.loadBeans();

            // TODO: register methods annotated @EventListener - get via output of MetadataExtractor, field of BeanDefinition

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
