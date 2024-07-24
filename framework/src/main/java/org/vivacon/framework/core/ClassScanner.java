package org.vivacon.framework.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    public static void main(String[] args) {
        List<File> classFiles = new ArrayList<>();

        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        for (String entry : classpathEntries) {
            File entryFile = new File(entry);
            if (entryFile.isDirectory()) {
                scanDirectory(entryFile, classFiles);
            } else if (entryFile.isFile() && entry.toLowerCase().endsWith(".jar")) {
                scanJarFile(entryFile, classFiles);
            }
        }

        for (File classFile : classFiles) {
            System.out.println(classFile.getAbsolutePath());
        }
    }

    private static void scanDirectory(File directory, List<File> classFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file, classFiles);
                } else if (file.isFile() && file.getName().toLowerCase().endsWith(".class")) {
                    classFiles.add(file);
                }
            }
        }
    }

    private static void scanJarFile(File jarFile, List<File> classFiles) {
        try (JarFile jf = new JarFile(jarFile)) {
            for (JarEntry entry : Collections.list(jf.entries())) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    File file = new File(entry.getName());
                    classFiles.add(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
