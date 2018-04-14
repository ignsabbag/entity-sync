package com.entitysync.model;

import javax.persistence.*;

/**
 * Default implementation of {@link AbstractEntityVersion}
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
@Entity
public class DefaultEntityVersion extends AbstractEntityVersion {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private Class<?> entity;
    @Version
    private Long version;
    private Long updateVersion;
    private Long commitVersion;

    private DefaultEntityVersion() {
        super(null);
    }

    DefaultEntityVersion(Class<?> entity) {
        super(entity);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Class<?> getEntity() {
        return entity;
    }

    @Override
    public void setEntity(Class<?> entity) {
        this.entity = entity;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public Long getUpdateVersion() {
        return updateVersion;
    }

    @Override
    public void setUpdateVersion(Long updateVersion) {
        this.updateVersion = updateVersion;
    }

    @Override
    public Long getCommitVersion() {
        return commitVersion;
    }

    @Override
    public void setCommitVersion(Long commitVersion) {
        this.commitVersion = commitVersion;
    }

}
