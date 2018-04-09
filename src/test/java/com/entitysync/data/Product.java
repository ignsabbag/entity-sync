package com.entitysync.data;

import com.entitysync.annotations.Sync;
import com.entitysync.annotations.SyncVersion;
import com.google.common.base.MoreObjects;

import javax.persistence.*;

/**
 * Created by nacho on 09/04/18.
 */
@Entity
@Sync(synchronizer = ProductSynchronizer.class, dependsOn = Brand.class)
public class Product {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    private Brand brand;
    @SyncVersion
    private Long syncVersion;

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

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Long getSyncVersion() {
        return syncVersion;
    }

    public void setSyncVersion(Long syncVersion) {
        this.syncVersion = syncVersion;
    }
}
