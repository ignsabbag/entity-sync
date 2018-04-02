package com.entitysync.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Holds the selected database for the current thread
 *
 * Created by ignsabbag on 10/04/16.
 */
public class DbContextHolder {

    private static final Logger log = LoggerFactory.getLogger(DbContextHolder.class);

    private static final ThreadLocal<DbType> contextHolder = new ThreadLocal<DbType>();

    public static void setDbType(DbType dbType) {
        if(dbType == null){
            throw new NullPointerException();
        }
        log.trace("Selected Database: " + dbType.name());
        contextHolder.set(dbType);
    }

    public static DbType getDbType() {
        return contextHolder.get();
    }

    public static void clearDbType() {
        log.trace("Selected Database: None (" + DbType.LOCAL.name() + ")");
        contextHolder.remove();
    }

    public static Boolean isLocalDbType() {
        return contextHolder.get() == null || contextHolder.get().equals(DbType.LOCAL);
    }

}
