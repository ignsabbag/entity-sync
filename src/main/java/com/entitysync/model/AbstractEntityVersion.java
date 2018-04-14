package com.entitysync.model;

/**
 * Represents the state of synchronization of an entity
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
public abstract class AbstractEntityVersion {

    public AbstractEntityVersion(Class<?> entity) {
        setEntity(entity);
        setCommitVersion(0L);
        setUpdateVersion(0L);
    }

    /**
     * Getter for the entity class to which this class represents
     */
    abstract public Class<?> getEntity();

    /**
     * Setter for the entity class to which this class represents
     */
    abstract public void setEntity(Class<?> entity);

    /**
     * Getter for the last version updated from central database
     */
    abstract public Long getUpdateVersion();

    /**
     * Setter for the last version updated from central database
     */
    abstract public void setUpdateVersion(Long updateVersion);

    /**
     * Getter for the last version committed to central database
     */
    abstract public Long getCommitVersion();

    /**
     * Setter for the last version committed to central database
     */
    abstract public void setCommitVersion(Long commitVersion);

    /**
     * Increases the commit version by one
     */
    public final Long incCommitVersion() {
        setCommitVersion(getCommitVersion() + 1);
        return getCommitVersion();
    }
}
