package com.entitysync;

/**
 * Service responsible for updating a entity from another.
 *
 * Created by ignsabbag on 16/04/16.
 */
public interface EntitySynchronizer<T> {

    /**
     * Creates or update an entity from another.
     * This method must be transactional.
     */
    T updateEntity(T entity);

}
