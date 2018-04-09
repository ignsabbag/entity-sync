package com.entitysync.data;

import com.entitysync.EntitySynchronizer;
import org.springframework.stereotype.Component;

/**
 * Created by nacho on 09/04/18.
 */
@Component
public class ProductSynchronizer implements EntitySynchronizer {

    @Override
    public Object updateEntity(Object entity) {
        return null;
    }
}
