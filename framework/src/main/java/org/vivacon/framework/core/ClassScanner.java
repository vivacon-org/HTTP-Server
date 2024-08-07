package org.vivacon.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
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

    public List<Class<?>> scanClassesAnnotatedBy(Set<Class<? extends Annotation>> annotations) {

        List<Class<?>> allClassesInClassPath = getAllClassesInClassPath();

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

    public List<Class<?>> getAllClassesInClassPath() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        List<File> classFiles = new ArrayList<>();
        for (String entry : classpathEntries) {

            File entryFile = new File(entry);

            if (entryFile.isDirectory()) {

                List<File> classesInDir = scanDirectory(entryFile);
                classFiles.addAll(classesInDir);

            } else if (entryFile.isFile() && entry.toLowerCase().endsWith(".jar")) {

                List<File> classesInJar = scanJarFile(entryFile);
                classFiles.addAll(classesInJar);
            }
        }

        // Convert file paths to class names and load classes
        List<Class<?>> classes = new ArrayList<>();
        for (File classFile : classFiles) {

            String className = convertFileToClassName(classFile);
            if (className == null) {
                continue;
            }

            try {
                Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                LOG.debug("Class not found of file {}", classFile);
            }
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

    public List<File> scanJarFile(File jarFile) {
        List<File> classesInJar = new LinkedList<>();
        try (JarFile jf = new JarFile(jarFile)) {
            for (JarEntry entry : Collections.list(jf.entries())) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    File file = new File(entry.getName());
                    classesInJar.add(file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classesInJar;
    }

    private String convertFileToClassName(File classFile) {
        String classpath = System.getProperty("java.class.path");
        String pathToFile = classFile.getAbsolutePath();

        int classpathIndex = pathToFile.indexOf(classpath);
        if (classpathIndex == -1) {
            return null;
        }

        String relativePath = pathToFile.substring(classpathIndex + classpath.length() + 1);

        // Remove the ".class" extension and replace file separators with dots
        String className = relativePath.replace(File.separatorChar, '.');
        if (className.endsWith(".class")) {
            className = className.substring(0, className.length() - ".class".length());
        }

        return className;
    }
}
