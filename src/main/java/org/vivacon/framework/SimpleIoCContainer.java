package org.vivacon.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class SimpleIoCContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIoCContainer.class);

    private Collection<String> classNames;

    private Map<String, Object> iocContainer;

    private Map<String, Method> endpointToMethod;

    private Map<String, Object> endpointToController;

    public SimpleIoCContainer() {
        this.classNames = new LinkedList<>();
        this.iocContainer = new HashMap<>();
        this.endpointToMethod = new HashMap<>();
        this.endpointToController = new HashMap<>();
        this.init();
    }

    public Method getEndpointHandler(String path) {
        return this.endpointToMethod.get(path);
    }

    public Object getController(String path) {
        return this.endpointToController.get(path);
    }

    public void init() {
        LOGGER.info("Do scanning");
        this.doScanning("org.vivacon.demo");
        LOGGER.info("Do instance");
        this.doInitializeInstances();
        LOGGER.info("Do inject");
        this.doInjectDependencies();
        LOGGER.info("Do mapping");
        this.doMappingPaths();
        LOGGER.info("Initiation is completed");
    }

    private void doScanning(String packageName) {
        URL resource = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = null;
        if (resource != null) {
            dir = new File(resource.getFile());
        }
        if (dir != null) {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file.isDirectory()) {
                    LOGGER.info("Scan in package " + packageName + "." + file.getName());
                    doScanning(packageName + "." + file.getName());
                } else {
                    String className = packageName + "." + file.getName().replaceAll("\\.class", "");
                    classNames.add(className);
                    LOGGER.info("Get class " + packageName + "." + file.getName());
                }
            }
        }
    }

    private void doInitializeInstances() {
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class)) {
                    this.iocContainer.put(className.toUpperCase(), clazz.getDeclaredConstructor().newInstance());
                }
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doInjectDependencies() {
        for (Map.Entry<String, Object> bean : this.iocContainer.entrySet()) {
            Field[] declaredFields = bean.getValue().getClass().getDeclaredFields();

            for (Field field : declaredFields) {
                String beanName = field.getType().getName().toUpperCase();
                field.setAccessible(true);
                Object requestedBean = this.iocContainer.get(beanName);
                try {
                    field.set(bean.getValue(), requestedBean);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void doMappingPaths() {
        for (Map.Entry<String, Object> bean : this.iocContainer.entrySet()) {
            Class<?> clazz = bean.getValue().getClass();

            if (clazz.isAnnotationPresent(Controller.class)) {
                String basePath = "";
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping classRequestMetaData = clazz.getAnnotation(RequestMapping.class);
                    basePath = classRequestMetaData.path();
                }

                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping methodRequestMetaData = method.getAnnotation(RequestMapping.class);
                        String fullPath = basePath + methodRequestMetaData.path();
                        this.endpointToMethod.put(fullPath, method);
                        this.endpointToController.put(fullPath, bean.getValue());
                    }
                }
            }
        }
    }

}
