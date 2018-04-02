package com.entitysync.utils;

import com.entitysync.annotations.Sync;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by ignsabbag on 24/12/16.
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
        return sync1.dependsOn().length - sync2.dependsOn().length;
    }

}
