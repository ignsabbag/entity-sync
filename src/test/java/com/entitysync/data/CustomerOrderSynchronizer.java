package com.entitysync.data;

import com.entitysync.EntitySynchronizer;
import org.springframework.stereotype.Component;

/**
 * Created by nacho on 09/04/18.
 */
@Component
public class CustomerOrderSynchronizer implements EntitySynchronizer<CustomerOrder> {

    @Override
    public CustomerOrder updateEntity(CustomerOrder entity) {
        return null;
    }
}
