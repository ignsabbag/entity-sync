package com.entitysync;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ignsabbag on 05/03/17.
 */
enum EntitiesHolder {

    INSTANCE;

    private final List<Object> entitiesHolder = Collections.synchronizedList(new ArrayList<>());

    static Boolean contains(Object object) {
        return INSTANCE.entitiesHolder.contains(object);
    }

    static void clear() {
        INSTANCE.entitiesHolder.clear();
    }

    static void addAll(Collection<?> collection) {
        INSTANCE.entitiesHolder.addAll(collection);
    }

}
