package com.entitysync.utils;

import java.util.Comparator;

/**
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
public class DummyComparator<T> implements Comparator<T> {

    public int compare(T o1, T o2) {
        return 0;
    }
}
