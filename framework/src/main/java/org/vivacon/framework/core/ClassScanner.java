package org.vivacon.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    private static final Logger LOG = LoggerFactory.getLogger(ClassScanner.class);

    public ClassScanner() {
        // Private constructor to prevent instantiation
    }

    private static final ClassScanner INSTANCE = new ClassScanner();

    public static ClassScanner getInstance() {
        return INSTANCE;
    }

    public List<Class<?>> scanClassesAnnotatedBy(Path scanningPath, Set<Class<? extends Annotation>> annotations) {

        List<Class<?>> allClassesInClassPath = getAllClassesInClassPath(scanningPath);

        List<Class<?>> manageBeanClasses = new LinkedList<>();

        for (Class<?> clazz : allClassesInClassPath) {

            if (clazz.isInterface()) {
                continue;
            }

            Annotation[] annotationsOfClazz = clazz.getAnnotations();

            for (Annotation annotationClazz : annotationsOfClazz) {

                if (annotations.contains(annotationClazz.annotationType())) {

                    manageBeanClasses.add(clazz);
                    break;
                }
            }
        }

        return manageBeanClasses;
    }

    public List<Class<?>> getAllClassesInClassPath(Path path) {
        String packageName = this.getClass().getPackage().getName();
        String packagePath = packageName.replace('.', '/');

        List<Class<?>> classes = new ArrayList<>();

        try {
            Enumeration<URL> resources = this.getClass().getClassLoader().getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                if (resource.getProtocol().equals("file")) {
                    // Handle directories
                    File packageDirectory = new File(resource.toURI());
                    List<File> classFiles = scanDirectory(packageDirectory);
                    classes.addAll(convertFilesToClasses(classFiles, packageName));

                } else if (resource.getProtocol().equals("jar")) {
                    // Handle JAR files
                    JarURLConnection jarConnection = (JarURLConnection) resource.openConnection();
                    JarFile jarFile = jarConnection.getJarFile();
                    classes.addAll(scanJarFile(jarFile, packagePath));
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to load classes from package: " + packageName, e);
        }

        return classes;
    }

    public List<File> scanDirectory(File rootDirectory) {
        Queue<File> directoriesToScan = new LinkedList<>();
        directoriesToScan.add(rootDirectory);

        List<File> classFiles = new LinkedList<>();
        while (!directoriesToScan.isEmpty()) {
            File currentDirectory = directoriesToScan.poll();

            File[] files = currentDirectory.listFiles();

            if (files == null) {
                continue;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    directoriesToScan.add(file);
                    continue;
                }

                if (file.isFile() && file.getName().toLowerCase().endsWith(".class")) {
                    classFiles.add(file);
                }
            }
        }

        return classFiles;
    }

    private List<Class<?>> scanJarFile(JarFile jarFile, String packagePath) {
        List<Class<?>> classes = new ArrayList<>();
        String packagePrefix = packagePath + "/";

        jarFile.stream()
                .filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".class") && entry.getName().startsWith(packagePrefix))
                .map(entry -> convertJarEntryToClassName(entry, packagePath))
                .forEach(className -> {
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Class not found: " + className, e);
                    }
                });

        return classes;
    }

    private String convertJarEntryToClassName(JarEntry entry, String packagePath) {
        String className = entry.getName().substring(packagePath.length() + 1, entry.getName().length() - 6);
        return packagePath.replace('/', '.') + "." + className.replace('/', '.');
    }

    private List<Class<?>> convertFilesToClasses(List<File> classFiles, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');

        // Get the base path for the package
        String basePath;
        try {
            basePath = new File(this.getClass().getClassLoader().getResource(packagePath).toURI()).getPath();
        } catch (URISyntaxException | NullPointerException e) {
            throw new RuntimeException("Failed to locate the package path for package: " + packageName, e);
        }
        for (File classFile : classFiles) {
            String filePath = classFile.getPath();

            if (!filePath.startsWith(basePath)) {
                continue;
            }

            String className = filePath.substring(basePath.length() + 1, filePath.length() - 6); // Remove base path and ".class"
            className = packageName + "." + className.replace(File.separatorChar, '.');

            try {
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                LOG.debug("Class not found for file {}", classFile);
            }
        }
        return classes;
    }
}
