package com.entitysync.db.builder;

import com.entitysync.db.DbType;
import com.entitysync.db.SyncRoutingDataSource;
import com.entitysync.db.provider.DataSourceProvider;
import com.google.common.collect.Maps;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

import static com.entitysync.db.DbType.CENTRAL;
import static com.entitysync.db.DbType.LOCAL;

/**
 * A builder that provides a convenient API for constructing an {@link DataSource}
 *
 * The DataSource created by this builder is an instance of {@link AbstractRoutingDataSource},
 * that has both local and central databases
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
public class SyncDataSourceBuilder {

    private DataSourceProvider localDataSourceProvider;
    private DataSourceProvider centralDataSourceProvider;

    /**
     * Return a builder to create a local data source provider
     */
    public DriverManagerDataSourceBuilder localDataSource() {
        return new DriverManagerDataSourceBuilder(this, LOCAL);
    }

    /**
     * Return a builder to create a central data source provider
     */
    public DriverManagerDataSourceBuilder centralDataSource() {
        return new DriverManagerDataSourceBuilder(this, CENTRAL);
    }

    SyncDataSourceBuilder dataSource(DbType dbType, DataSourceProvider provider) {
        switch (dbType) {
            case LOCAL:
                localDataSource(provider);
                break;
            case CENTRAL:
                centralDataSource(provider);
                break;
        }
        return this;
    }

    public SyncDataSourceBuilder localDataSource(DataSourceProvider provider) {
        localDataSourceProvider = provider;
        return this;
    }

    public SyncDataSourceBuilder centralDataSource(DataSourceProvider provider) {
        centralDataSourceProvider = provider;
        return this;
    }

    public DataSource build() {
        AbstractRoutingDataSource routingDataSource = new SyncRoutingDataSource();
        Map<Object, Object> targetDataSources = Maps.newHashMap();
        targetDataSources.put(LOCAL, localDataSourceProvider.getDataSource());
        targetDataSources.put(CENTRAL, centralDataSourceProvider.getDataSource());
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(localDataSourceProvider.getDataSource());
        return routingDataSource;
    }
}
