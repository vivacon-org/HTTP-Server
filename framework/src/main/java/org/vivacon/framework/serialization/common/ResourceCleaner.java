package org.vivacon.framework.serialization.common;

import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class ResourceCleaner {

    private static final Cleaner cleaner = Cleaner.create();

    private static Queue<WeakReference<Cleaner.Cleanable>> cleanableQueue = new LinkedList<>();

    /**
     * Use to register an object and its resources, before the time the object is collected by GC, the resources will be closed safely
     */
    public static void register(Object object, AutoCloseable... resources) {
        Cleaner.Cleanable register = cleaner.register(object, new ResourceCleanup(resources));
        cleanableQueue.add(new WeakReference<>(register));
    }

    private record ResourceCleanup(AutoCloseable[] resources) implements Runnable {

        @Override
        public void run() {
            for (AutoCloseable resource : resources) {
                try {
                    resource.close();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to close resource: " + resource, e);
                }
            }
        }
    }

    /**
     * Use when we want to clean resources manually
     */
    public static void cleanResources() {
        Iterator<WeakReference<Cleaner.Cleanable>> iterator = cleanableQueue.iterator();

        while (iterator.hasNext()) {
            WeakReference<Cleaner.Cleanable> weakRef = iterator.next();
            Cleaner.Cleanable cleanable = weakRef.get();

            if (cleanable != null) {
                cleanable.clean();
            }

            iterator.remove();
        }
    }
}
