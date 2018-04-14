package com.entitysync;

/**
 * @author Ignacio Sabbag
 * @since 1.0
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
