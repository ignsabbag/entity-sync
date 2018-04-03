package com.entitysync.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ignsabbag on 09/04/16.
 */
@Repository
public interface DefaultEntityVersionRepository
        extends EntityVersionRepository, JpaRepository<DefaultEntityVersion, Long> {

    DefaultEntityVersion findByEntity(Class<?> entityClass);

    @Override
    default AbstractEntityVersion newEntityVersion(Class<?> entityClass) {
        return new DefaultEntityVersion(entityClass);
    }

}
