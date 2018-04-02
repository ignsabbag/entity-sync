package com.entitysync.db.provider;

import javax.sql.DataSource;

/**
 * Provides a {@link DataSource} given a set of configuration parameters
 *
 * Created by ignacios on 11/10/2016.
 */
public abstract class DataSourceProvider {

    private DataSource dataSource;

    public DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (getClass()) {
                if (dataSource == null) {
                    dataSource = createDataSource();
                }
            }
        }
        return dataSource;
    }

    protected abstract DataSource createDataSource();
}
