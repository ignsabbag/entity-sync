package com.entitysync.model;

/**
 * Created by ignsabbag on 09/04/16.
 */
public interface EntityVersionRepository {

    AbstractEntityVersion findByEntity(Class<?> entityClass);

    AbstractEntityVersion newEntityVersion(Class<?> entityClass);

    AbstractEntityVersion save(AbstractEntityVersion entityVersion);

    default AbstractEntityVersion getEntityVersion(Class<?> entityClass) {
        AbstractEntityVersion entityVersion = findByEntity(entityClass);
        if (entityVersion == null) {
            entityVersion = newEntityVersion(entityClass);
            save(entityVersion);
        }
        return entityVersion;
    }

}
