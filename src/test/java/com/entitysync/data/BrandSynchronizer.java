package com.entitysync.data;

import com.entitysync.EntitySynchronizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by ignsabbag on 16/04/16.
 */
@Component
public class BrandSynchronizer implements EntitySynchronizer<Brand> {

    @Autowired
    BrandRepository brandRepository;

    @Override
    public Brand updateEntity(Brand aBrand) {
        Brand otherBrand = brandRepository.findByName(aBrand.getName());
        if (otherBrand == null) {
            otherBrand = new Brand();
        }
        otherBrand.setName(aBrand.getName());
        brandRepository.save(otherBrand);
        return otherBrand;
    }

}
