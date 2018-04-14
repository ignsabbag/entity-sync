package com.entitysync.utils;

import com.entitysync.annotations.Sync;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author Ignacio Sabbag
 * @since 1.0
 */
public class SyncComparator implements Comparator<Class<?>> {

    public int compare(Class<?> clazz1, Class<?> clazz2) {
        Sync sync1 = clazz1.getAnnotation(Sync.class);
        Sync sync2 = clazz2.getAnnotation(Sync.class);

        if (Arrays.asList(sync1.dependsOn()).contains(clazz2)) {
            return 1;
        }
        if (Arrays.asList(sync2.dependsOn()).contains(clazz1)) {
            return -1;
        }
        for (Class<?> entityClass : sync1.dependsOn()) {
            int result = compare(entityClass, clazz2);
            if (result > 0) {
                return result;
            }
        }
        for (Class<?> entityClass : sync2.dependsOn()) {
            int result = compare(clazz1, entityClass);
            if (result < 0) {
                return result;
            }
        }
        return 0;
    }

}
