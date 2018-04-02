package com.entitysync.db.builder;

import com.entitysync.db.DbType;
import com.entitysync.db.provider.DriverManagerDataSourceProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ignsabbag on 12/03/17.
 */
public class DriverManagerDataSourceBuilder {

    private String driverClassName;
    private String databaseUrl;
    private String username;
    private String password;

    private final SyncDataSourceBuilder builder;
    private final DbType dbType;

    DriverManagerDataSourceBuilder(SyncDataSourceBuilder builder, DbType dbType) {
        this.builder = builder;
        this.dbType = dbType;
    }

    public DriverManagerDataSourceBuilder driverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    public DriverManagerDataSourceBuilder databaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        return this;
    }

    public DriverManagerDataSourceBuilder username(String username) {
        this.username = username;
        return this;
    }

    public DriverManagerDataSourceBuilder password(String password) {
        this.password = password;
        return this;
    }

    public SyncDataSourceBuilder and() {
        afterPropertiesSet();
        return builder.dataSource(dbType,
                new DriverManagerDataSourceProvider(driverClassName, databaseUrl, username, password));
    }

    private void afterPropertiesSet() {
        checkNotNull(driverClassName);
        checkNotNull(databaseUrl);
        checkNotNull(username);
        checkNotNull(password);
    }
}
