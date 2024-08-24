package org.vivacon.framework.core;

import org.vivacon.framework.bean.BeanFactory;
import org.vivacon.framework.bean.BeansInitiationOrderResolver;
import org.vivacon.framework.bean.IoCContainer;
import org.vivacon.framework.bean.MetadataExtractor;
import org.vivacon.framework.bean.annotation.Component;
import org.vivacon.framework.bean.annotation.Service;
import org.vivacon.framework.web.annotation.RestController;

import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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

            // TODO: register methods annotated @EventListener - get via output of MetadataExtractor, field of BeanDefinition

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
