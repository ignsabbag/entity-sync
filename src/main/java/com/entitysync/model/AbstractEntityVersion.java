package com.entitysync.model;

/**
 * Represents the state of synchronization of an entity
 *
 * Created by ignsabbag on 12/03/17.
 */
public abstract class AbstractEntityVersion {

    public AbstractEntityVersion(Class<?> entity) {
        setEntity(entity);
        setCommitVersion(0L);
        setUpdateVersion(0L);
    }

    abstract public Class<?> getEntity();

    abstract public void setEntity(Class<?> entity);

    abstract public Long getUpdateVersion();

    abstract public void setUpdateVersion(Long updateVersion);

    abstract public Long getCommitVersion();

    abstract public void setCommitVersion(Long commitVersion);

    public final Long incCommitVersion() {
        setCommitVersion(getCommitVersion() + 1);
        return getCommitVersion();
    }
}
