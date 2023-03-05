package org.vivacon.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Engine {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Collection<String> classNames;
    private Map<String, Object> iocContainer;
    private Map<String, Method> endpointToMethod;
    private Map<String, Object> endpointToController;

    public Engine() {
        classNames = new LinkedList<>();
        iocContainer = new HashMap<>();
        endpointToMethod = new HashMap<>();
        endpointToController = new HashMap<>();
        init();
    }

    public Method getEndpointHandler(String path) {
        return endpointToMethod.get(path);
    }

    public Object getController(String path) {
        return endpointToController.get(path);
    }

    public void init() {
        logger.info("Do scanning");
        doScanning("org.hungdoan");
        logger.info("Do instance");
        doInstance();
        logger.info("Do inject");
        doInject();
        logger.info("Do mapping");
        doMapping();
        logger.info("Initiation is completed");
    }

    private void doScanning(String packageName) {
        URL resource = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {

                logger.info("Scan in package " + packageName + "." + file.getName());
                doScanning(packageName + "." + file.getName());
            } else {
                String className = packageName + "." + file.getName().replaceAll("\\.class", "");
                classNames.add(className);
                logger.info("Get class " + packageName + "." + file.getName());
            }
        }
    }

    private void doInstance() {
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class)) {
                    iocContainer.put(className.toUpperCase(), clazz.getDeclaredConstructor().newInstance());
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doInject() {

        for (Map.Entry<String, Object> bean : iocContainer.entrySet()) {
            Field[] declaredFields = bean.getValue().getClass().getDeclaredFields();

            for (Field field : declaredFields) {

                String beanName = field.getType().getName().toUpperCase();
                field.setAccessible(true);
                Object requestedBean = iocContainer.get(beanName);
                try {
                    field.set(bean.getValue(), requestedBean);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void doMapping() {

        for (Map.Entry<String, Object> bean : iocContainer.entrySet()) {

            Class<? extends Object> clazz = bean.getValue().getClass();

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
                        endpointToMethod.put(fullPath, method);
                        endpointToController.put(fullPath, bean.getValue());
                    }
                }
            }
        }
    }
}
