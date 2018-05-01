package com.entitysync.data;

import com.entitysync.EntitySynchronizer;
import org.springframework.stereotype.Component;

/**
 * Created by nacho on 09/04/18.
 */
@Component
public class UserSynchronizer implements EntitySynchronizer<User> {

    @Override
    public User updateEntity(User entity) {
        return null;
    }
}
