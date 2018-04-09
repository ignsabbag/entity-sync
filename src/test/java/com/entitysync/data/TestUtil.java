package com.entitysync.data;

import com.entitysync.db.DbContextHolder;
import com.entitysync.db.DbType;
import com.entitysync.model.DefaultEntityVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by nacho on 22/11/17.
 */
@Service
public class TestUtil {

    private final BrandRepository brandRepository;

    private final DefaultEntityVersionRepository entityVersionRepository;

    @Autowired
    public TestUtil(BrandRepository brandRepository, DefaultEntityVersionRepository entityVersionRepository) {
        this.brandRepository = brandRepository;
        this.entityVersionRepository = entityVersionRepository;
    }

    public void deleteAll() {
        DbContextHolder.setDbType(DbType.LOCAL);
        brandRepository.deleteAll();
        entityVersionRepository.deleteAll();
        DbContextHolder.setDbType(DbType.CENTRAL);
        brandRepository.deleteAll();
        entityVersionRepository.deleteAll();
        DbContextHolder.clearDbType();
    }

}
