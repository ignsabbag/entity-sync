package com.entitysync.utils;

import com.entitysync.annotations.SyncVersion;

import java.lang.reflect.Field;

/**
 * Created by ignsabbag on 08/05/16.
 */
public class EntityUtils {

    public static Class<?> getEntityClass(String entityClass) {
        try {
            return Class.forName(entityClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Long getCommitVersion(Object entity) {
        try {
            Field field = getCommitVersionField(entity);
            field.setAccessible(true);
            return (Long) field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setCommitVersion(Object entity, Long commitVersion) {
        try {
            Field field = getCommitVersionField(entity);
            field.setAccessible(true);
            field.set(entity, commitVersion);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getCommitVersionField(Object entity) {
        return getCommitVersionField(entity.getClass());
    }

    public static Field getCommitVersionField(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(SyncVersion.class)) {
                return field;
            }
        }
        throw new RuntimeException("@SyncVersion annotation not found");
    }
}
