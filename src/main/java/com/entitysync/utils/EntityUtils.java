package com.entitysync.utils;

import com.entitysync.annotations.SyncVersion;

import java.lang.reflect.Field;

/**
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
public class EntityUtils {

    public static Class<?> getEntityClass(String entityClass) {
        try {
            return Class.forName(entityClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Long getSyncVersion(Object entity) {
        try {
            Field field = getSyncVersionField(entity);
            field.setAccessible(true);
            return (Long) field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setSyncVersion(Object entity, Long commitVersion) {
        try {
            Field field = getSyncVersionField(entity);
            field.setAccessible(true);
            field.set(entity, commitVersion);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getSyncVersionField(Object entity) {
        return getSyncVersionField(entity.getClass());
    }

    public static Field getSyncVersionField(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(SyncVersion.class)) {
                return field;
            }
        }
        throw new RuntimeException("@SyncVersion annotation not found");
    }
}
