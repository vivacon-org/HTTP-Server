package org.vivacon.framework.core;

import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated;
import org.vivacon.framework.bean.BeanFactory;
import org.vivacon.framework.bean.BeansInitiationOrderResolver;
import org.vivacon.framework.bean.IoCContainer;
import org.vivacon.framework.bean.MetadataExtractor;
import org.vivacon.framework.bean.annotations.Component;
import org.vivacon.framework.bean.annotations.Service;
import org.vivacon.framework.web.annotations.Controller;

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
            URL location = mainClass.getProtectionDomain().getCodeSource().getLocation();
            Set<Class<? extends Annotation>> managedAnnotations = new HashSet<>();
            managedAnnotations.add(Controller.class);
            managedAnnotations.add(Service.class);
            managedAnnotations.add(Component.class);
            Path scanningPath = Path.of(location.toURI());
            IoCContainer ioCContainer = new IoCContainer(ClassScanner.getInstance(), MetadataExtractor.getInstance(), BeanFactory.getInstance(), BeansInitiationOrderResolver.getInstance(), scanningPath, managedAnnotations);
            ioCContainer.loadBeans();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
