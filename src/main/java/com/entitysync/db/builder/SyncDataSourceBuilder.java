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
 * Created by ignsabbag on 12/03/17.
 */
public class SyncDataSourceBuilder {

    private DataSourceProvider localDataSourceProvider;
    private DataSourceProvider centralDataSourceProvider;

    public DriverManagerDataSourceBuilder localDataSource() {
        return new DriverManagerDataSourceBuilder(this, LOCAL);
    }

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
