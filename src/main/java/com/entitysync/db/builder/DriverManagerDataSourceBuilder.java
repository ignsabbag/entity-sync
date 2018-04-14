package com.entitysync.db.builder;

import com.entitysync.db.DbType;
import com.entitysync.db.provider.DriverManagerDataSourceProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a way of constructing a {@link DriverManagerDataSourceProvider}
 * using the builder pattern
 *
 * @author Ignacio Sabbag
 * @since 1.0
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

    /**
     * Set the driver class name to be used on this data source
     */
    public DriverManagerDataSourceBuilder driverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    /**
     * Set the url used to connect to the database
     */
    public DriverManagerDataSourceBuilder databaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        return this;
    }

    /**
     * Set the username used to log into the database
     */
    public DriverManagerDataSourceBuilder username(String username) {
        this.username = username;
        return this;
    }

    /**
     * Set the password used to log into the database
     */
    public DriverManagerDataSourceBuilder password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Build a instance of {@link DriverManagerDataSourceProvider} with the
     * provided data and return the instance of {@link SyncDataSourceBuilder}
     */
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
