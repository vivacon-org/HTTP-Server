package org.vivacon.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

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
        List<Class<?>> classes = new ArrayList<>();

        try {
            if (Files.isDirectory(path)) {
                List<File> classFiles = scanDirectory(path.toFile());
                classes.addAll(convertFilesToClasses(classFiles, path));
            } else if (path.toString().endsWith(".jar")) {
                try (JarFile jarFile = new JarFile(path.toFile())) {
                    classes.addAll(scanJarFile(jarFile));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load classes from path: " + path, e);
        }

        return classes;
    }

    public List<File> scanDirectory(File rootDirectory) {
        List<File> classFiles = new LinkedList<>();

        try (Stream<Path> paths = Files.walk(rootDirectory.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".class"))
                    .forEach(path -> classFiles.add(path.toFile()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan directory: " + rootDirectory, e);
        }

        return classFiles;
    }

    private List<Class<?>> scanJarFile(JarFile jarFile) {
        List<Class<?>> classes = new ArrayList<>();

        jarFile.stream()
                .filter(entry -> !entry.isDirectory() && entry.getName().endsWith(".class"))
                .map(this::convertJarEntryToClassName)
                .forEach(className -> {
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Class not found: " + className, e);
                    }
                });

        return classes;
    }

    private String convertJarEntryToClassName(JarEntry entry) {
        String className = entry.getName().replace('/', '.');
        return className.substring(0, className.length() - ".class".length());
    }

    private List<Class<?>> convertFilesToClasses(List<File> classFiles, Path rootPath) {
        List<Class<?>> classes = new ArrayList<>();

        for (File file : classFiles) {
            String className = convertFileToClassName(file, rootPath);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found: " + className, e);
            }
        }
        return classes;
    }

    private String convertFileToClassName(File file, Path rootPath) {
        Path relativePath = rootPath.relativize(file.toPath());
        String className = relativePath.toString()
                .replace(File.separatorChar, '.');
        return className.substring(0, className.length() - ".class".length());
    }
}
