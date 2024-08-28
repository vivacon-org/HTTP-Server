package org.vivacon.framework.common;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClassLoaderFactory {
    private static final ClassLoaderFactory INSTANCE = new ClassLoaderFactory();

    public static ClassLoaderFactory getInstance() {
        return INSTANCE;
    }

    private final Map<String, ClassLoader> pathsToClassLoader;

    public ClassLoaderFactory() {
        this.pathsToClassLoader = new HashMap<>();
    }

    //TODO rename reduce
    public ClassLoader create(URL... paths) {
        Optional<String> reduce = Arrays.stream(paths).map(URL::getPath).reduce((s, s2) -> s + "," + s2);
        if (reduce.isEmpty()) {
            throw new IllegalArgumentException("Invalid path " + Arrays.toString(paths));
        }
        String key = reduce.get();
        ClassLoader classLoader = pathsToClassLoader.get(key);
        if (classLoader != null){
            return classLoader;
        }
        URLClassLoader urlClassLoader = new URLClassLoader(paths);
        pathsToClassLoader.put(key, urlClassLoader);
        return urlClassLoader;
    }
}
