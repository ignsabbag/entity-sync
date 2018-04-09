package com.entitysync;

import com.entitysync.data.Brand;
import com.entitysync.data.BrandRepository;
import com.entitysync.data.TestUtil;
import com.entitysync.db.DbContextHolder;
import com.entitysync.db.DbType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class SyncServiceTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private SyncService syncService;

    @Before
    public void initializeDb() {
        testUtil.deleteAll();
    }

    @Test
    public void testLocalChange() {
        Brand brand = new Brand("brand 1");
        brandRepository.save(brand);
        assertNotNull(brand.getId());
        assertTrue(brand.getCommitVersion() > 0);
        Brand localBrand = assertBrandCreated(brand);

        assertEquals(0, syncService.syncEntities());
        DbContextHolder.setDbType(DbType.CENTRAL);
        Brand centralBrand = assertBrandCreated(brand);

        assertNotEquals(localBrand, centralBrand);
    }

    @Test
    public void testCentralChange() {
        DbContextHolder.setDbType(DbType.CENTRAL);
        Brand brand = new Brand("brand 1");
        brand.setCommitVersion(1L);
        brandRepository.save(brand);
        assertNotNull(brand.getId());

        Brand centralBrand = assertBrandCreated(brand);

        assertEquals(0, syncService.syncEntities());

        DbContextHolder.setDbType(DbType.LOCAL);
        Brand localBrand = assertBrandCreated(brand);

        assertNotEquals(localBrand, centralBrand);
    }

    @Test
    public void testBothChange() {
        Brand brand = new Brand("brand 1");
        brandRepository.save(brand);
        assertNotNull(brand.getId());
        assertTrue(brand.getCommitVersion() > 0);
        assertBrandCreated(brand);

        DbContextHolder.setDbType(DbType.CENTRAL);
        brandRepository.save(brand);
        assertNotNull(brand.getId());
        assertBrandCreated(brand);

        assertEquals(0, syncService.syncEntities());
    }

    private Brand assertBrandCreated(Brand brand) {
        List<Brand> brands = brandRepository.findAll();
        assertNotNull(brands);
        assertEquals(1, brands.size());
        Brand otherBrand = brands.get(0);
        assertEquals(brand.getName(), otherBrand.getName());
        return otherBrand;
    }

}
