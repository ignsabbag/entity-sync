package com.entitysync.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ignsabbag on 13/02/16.
 */
@Repository
@Transactional
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Brand findByName(String name);

}
