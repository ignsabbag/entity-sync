package com.entitysync.utils;

import com.entitysync.db.DbContextHolder;
import com.entitysync.db.DbType;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

/**
 * Created by nacho on 01/05/18.
 */
public class CentralCommand {

    @Transactional
    public static <T> T doInCentral(Supplier<T> supplier) {
        try {
            DbContextHolder.setDbType(DbType.CENTRAL);
            return supplier.get();
        } finally {
            DbContextHolder.clearDbType();
        }
    }

}
