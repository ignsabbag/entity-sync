package com.entitysync;

/**
 * Created by ignsabbag on 05/03/17.
 */
enum EntitiesHolder {

    INSTANCE;

    private final ThreadLocal<Class<?>> holder = new ThreadLocal<>();

    static Boolean contains(Class<?> aClass) {
        return INSTANCE.holder.get() != null && INSTANCE.holder.get().equals(aClass);
    }

    static void set(Class<?> aClass) {
        INSTANCE.holder.set(aClass);
    }

    static void remove() {
        INSTANCE.holder.remove();
    }

}
