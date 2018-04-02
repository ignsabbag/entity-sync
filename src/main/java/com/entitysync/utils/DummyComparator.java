package com.entitysync.utils;

import java.util.Comparator;

/**
 * Created by ignsabbag on 19/02/17.
 */
public class DummyComparator<T> implements Comparator<T> {

    public int compare(T o1, T o2) {
        return 0;
    }
}
