package com.entitysync;

import com.entitysync.annotations.Sync;
import com.entitysync.utils.EntityUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by ignsabbag on 09/04/16.
 */
public class SyncInterceptor extends EmptyInterceptor {

    private static SyncService syncService;

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        return updateCommitVersion(entity, state, propertyNames);
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        return updateCommitVersion(entity, currentState, propertyNames);
    }

    private boolean updateCommitVersion(Object entity, Object[] state, String[] propertyNames) {
        if (isSynchronizedEntity(entity)) {
            Field field = EntityUtils.getCommitVersionField(entity);
            for (int i = 0; i < propertyNames.length; i++) {
                if (field.getName().equals(propertyNames[i])) {
                    Long commitVersion = syncService.getCommitVersion(entity);
                    if (commitVersion != null) {
                        state[i] = commitVersion;
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean isSynchronizedEntity(Object entity) {
        return AnnotationUtils.isAnnotationDeclaredLocally(Sync.class, entity.getClass());
    }

    @Autowired
    public void setSyncService(SyncService syncService) {
        SyncInterceptor.syncService = syncService;
    }
}
