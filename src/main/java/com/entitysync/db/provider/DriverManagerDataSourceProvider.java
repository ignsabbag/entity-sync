package com.entitysync.db.provider;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Provides an instance of {@link DriverManagerDataSource} given a <code>driverClassName</code>,
 * a <code>databaseUrl</code>, <code>username</code> and <code>password</code>
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
public class DriverManagerDataSourceProvider extends DataSourceProvider {

    private final String driverClassName;

    private final String databaseUrl;

    private final String username;

    private final String password;

    public DriverManagerDataSourceProvider(String driverClassName, String databaseUrl, String username, String password) {
        this.driverClassName = driverClassName;
        this.databaseUrl = databaseUrl;
        this.username = username;
        this.password = password;
    }

    protected DataSource createDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

}
