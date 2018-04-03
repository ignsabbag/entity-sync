package com.entitysync.data;

import com.entitysync.annotations.Sync;
import com.entitysync.annotations.SyncVersion;
import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by ignsabbag on 13/02/16.
 */
@Entity
@Sync(synchronizer = BrandSynchronizer.class)
public class Brand {
    @Id @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;
    @SyncVersion
    private Long commitVersion;

    public Brand() {
    }

    public Brand(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCommitVersion() {
        return commitVersion;
    }

    public void setCommitVersion(Long commitVersion) {
        this.commitVersion = commitVersion;
    }
}
