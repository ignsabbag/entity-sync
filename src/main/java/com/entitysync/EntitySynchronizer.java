package com.entitysync;

/**
 * Service responsible for updating a entity from another.
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
public interface EntitySynchronizer<T> {

    /**
     * Creates or update an entity from a database to another.
     * This method must be transactional.
     */
    T updateEntity(T entity);

}
