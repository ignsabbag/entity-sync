package com.entitysync;

import com.entitysync.annotations.Sync;
import com.entitysync.db.DbContextHolder;
import com.entitysync.db.DbType;
import com.entitysync.model.AbstractEntityVersion;
import com.entitysync.model.EntityVersionRepository;
import com.entitysync.utils.DummyComparator;
import com.entitysync.utils.EntityUtils;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Ignacio Sabbag
 * @since 1.0
 */
@Service
public class SyncEntityService implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    private final EntityVersionRepository entityVersionRepository;

    private ApplicationContext appContext;

    SyncEntityService(EntityVersionRepository entityVersionRepository) {
        this.entityVersionRepository = entityVersionRepository;
    }


    Void syncEntitiesForClass(Class<?> entityClass) {
        DbContextHolder.clearDbType();
        AbstractEntityVersion entityVersion = entityVersionRepository.getEntityVersion(entityClass);
        logger.trace("Starting the synchronization of the entity " + entityVersion.getEntity().toString());

        logger.debug("Searching for locally modified entities...");
        List dirtyEntities = loadEntities(entityVersion);

        logger.debug("Searching for remotely modified entities...");
        List remoteEntities = doInCentral(() -> loadEntities(entityVersion));

        EntitiesHolder.set(entityClass);
        perform(entityVersion, dirtyEntities, remoteEntities);

        return null;
    }

    @Transactional
    private List loadEntities(AbstractEntityVersion entityVersion) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(entityVersion.getEntity());
        Root<?> entity = cq.from(entityVersion.getEntity());
        String commitVersionField = EntityUtils.getSyncVersionField(entityVersion.getEntity()).getName();
        cq.where(
                cb.or(
                        cb.greaterThan(entity.get(commitVersionField), entityVersion.getUpdateVersion()),
                        cb.isNull(entity.get(commitVersionField))));
        List result = entityManager.createQuery(cq).getResultList();
        logger.debug(result.size() + " entities found");
        return result;
    }

    private <T> void perform(AbstractEntityVersion entityVersion, List<T> dirtyEntities, List<T> remoteEntities) {
        //Filtro las entidades modificadas localmente
        remoteEntities.removeAll(dirtyEntities);

        logger.debug("Updating " + remoteEntities.size() + " entities modified remotely");
        entityVersion = updateEntities(entityVersion, remoteEntities);

        logger.debug("Updating " + dirtyEntities.size() + " entities modified locally");
        commitEntities(entityVersion, dirtyEntities);
    }

    @Transactional(rollbackFor = Throwable.class)
    private <T> AbstractEntityVersion updateEntities(AbstractEntityVersion entityVersion, List<T> remoteEntities) {
        EntitySynchronizer<T> synchronizer = getEntitySynchronizer(entityVersion.getEntity());
        logger.trace("Using syncronizer: " + synchronizer.getClass().getName());
        Comparator<T> comparator = getEntityComparator(entityVersion.getEntity());
        logger.trace("Using comparator: " + comparator.getClass().getName());
        remoteEntities.sort(comparator);
        remoteEntities.forEach(entity -> updateEntity(entity, synchronizer, entityVersion));

        if (entityVersion.getUpdateVersion() > entityVersion.getCommitVersion()) {
            logger.debug("Updating CommitVersion, from " + entityVersion.getCommitVersion()
                    + " to " + entityVersion.getUpdateVersion());
            entityVersion.setCommitVersion(entityVersion.getUpdateVersion());
        }
        return entityVersionRepository.save(entityVersion);
    }

    private <T> void updateEntity(T entity, EntitySynchronizer<T> synchronizer, AbstractEntityVersion entityVersion) {
        logger.info("Updating local entity: " + entity.toString());
        synchronizer.updateEntity(entity);
        Long commitVersion = EntityUtils.getSyncVersion(entity);
        if (entityVersion.getUpdateVersion() < commitVersion) {
            entityVersion.setUpdateVersion(commitVersion);
        }
    }

    private <T> void commitEntities(AbstractEntityVersion entityVersion, List<T> entities) {
        EntitySynchronizer<T> synchronizer = getEntitySynchronizer(entityVersion.getEntity());
        logger.trace("Using syncronizer: " + synchronizer.getClass().getName());
        Comparator<T> comparator = getEntityComparator(entityVersion.getEntity());
        logger.trace("Using comparator: " + comparator.getClass().getName());
        entities.sort(comparator);
        entities.forEach(entity -> commitEntity(entity, synchronizer, entityVersion));

        logger.debug("Updating UpdateVersion from " + entityVersion.getUpdateVersion()
                + " to " + entityVersion.getCommitVersion());
        entityVersion.setUpdateVersion(entityVersion.getCommitVersion());
        entityVersionRepository.save(entityVersion);
    }

    private <T> void commitEntity(T entity, EntitySynchronizer<T> synchronizer, AbstractEntityVersion entityVersion) {
        logger.info("Updating remote entity: " + entity.toString());
        Long nextVersion = entityVersion.incCommitVersion();
        EntityUtils.setSyncVersion(entity, nextVersion);
        doInCentral(() -> synchronizer.updateEntity(entity));
        synchronizer.updateEntity(entity);
    }

    private <T> EntitySynchronizer<T> getEntitySynchronizer(Class<?> entityClass) {
        Sync sync = AnnotationUtils.findAnnotation(entityClass, Sync.class);
        EntitySynchronizer<T> synchronizer = appContext.getBean(sync.synchronizer());
        Preconditions.checkState(synchronizer != null,
                "Unable to find synchronizer named: " + sync.synchronizer().getName());
        return synchronizer;
    }

    private <T> Comparator<T> getEntityComparator(Class<?> entityClass) {
        try {
            Sync sync = AnnotationUtils.findAnnotation(entityClass, Sync.class);
            return sync.comparator().newInstance();
        } catch (Exception e) {
            logger.warn("Unable to initialize the comparator", e);
        }
        return new DummyComparator<T>();
    }

    @Transactional
    private <T> T doInCentral(Supplier<T> supplier) {
        try {
            DbContextHolder.setDbType(DbType.CENTRAL);
            return supplier.get();
        } finally {
            DbContextHolder.clearDbType();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
}
