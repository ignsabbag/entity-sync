package com.entitysync;

import com.entitysync.annotations.Sync;
import com.entitysync.db.DbContextHolder;
import com.entitysync.model.AbstractEntityVersion;
import com.entitysync.model.EntityVersionRepository;
import com.entitysync.utils.EntityUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Intercepts an instance before get saved to increment the entity sync version.
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
public class SyncInterceptor extends EmptyInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private EntityVersionRepository entityVersionRepository;

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
            Field field = EntityUtils.getSyncVersionField(entity);
            for (int i = 0; i < propertyNames.length; i++) {
                if (field.getName().equals(propertyNames[i])) {
                    Long commitVersion = getCommitVersion(entity);
                    if (commitVersion != null) {
                        state[i] = commitVersion;
                        logger.debug("New version for entity {}: {}", entity.getClass().getName(), commitVersion);
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

    /**
     * Increment the number of version y return the value to be stored on the entity
     */
    private Long getCommitVersion(Object object) {
        if (!DbContextHolder.isLocalDbType() || EntitiesHolder.contains(object.getClass())) {
            return null;
        }

        synchronized (object.getClass()) {
            AbstractEntityVersion entityVersion = entityVersionRepository.getEntityVersion(object.getClass());
            if (entityVersion != null) {
                Long nextVersion = entityVersion.incCommitVersion();
                entityVersionRepository.save(entityVersion);
                return nextVersion;
            }
        }
        return 1L;
    }

    @Autowired
    public void setEntityVersionRepository(EntityVersionRepository entityVersionRepository) {
        this.entityVersionRepository = entityVersionRepository;
    }
}
