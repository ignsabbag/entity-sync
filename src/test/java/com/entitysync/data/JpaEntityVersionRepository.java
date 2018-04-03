package com.entitysync.data;

import com.entitysync.model.AbstractEntityVersion;
import com.entitysync.model.EntityVersionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ignsabbag on 09/04/16.
 */
@Repository
public interface JpaEntityVersionRepository
        extends EntityVersionRepository, JpaRepository<EntityVersion, Long> {

    EntityVersion findByEntity(Class<?> entityClass);

    @Override
    default AbstractEntityVersion newEntityVersion(Class<?> entityClass) {
        return new EntityVersion(entityClass);
    }

}
