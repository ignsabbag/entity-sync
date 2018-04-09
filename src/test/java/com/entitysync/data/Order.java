package com.entitysync.data;

import com.entitysync.annotations.Sync;
import com.entitysync.annotations.SyncVersion;
import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by nacho on 09/04/18.
 */
@Entity
@Sync(synchronizer = OrderSynchronizer.class, dependsOn = Product.class)
public class Order {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany
    private Set<Product> products;
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

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Long getSyncVersion() {
        return syncVersion;
    }

    public void setSyncVersion(Long syncVersion) {
        this.syncVersion = syncVersion;
    }
}
