package com.entitysync;

import com.entitysync.annotations.Sync;
import com.entitysync.config.SyncEntities;
import com.entitysync.db.DbContextHolder;
import com.entitysync.db.DbType;
import com.entitysync.model.AbstractEntityVersion;
import com.entitysync.model.EntityVersionRepository;
import com.entitysync.utils.DummyComparator;
import com.entitysync.utils.EntityUtils;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Created by ignsabbag on 09/04/16.
 */
public class SyncService implements ApplicationContextAware {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private EntityVersionRepository entityVersionRepository;

    private SyncEntities syncEntities;

    @PersistenceContext
    private EntityManager entityManager;

    private ApplicationContext appContext;

    private Retryer<Void> retryer;

    @Autowired
    public SyncService(EntityVersionRepository entityVersionRepository, SyncEntities syncEntities) {
        this.entityVersionRepository = entityVersionRepository;
        this.syncEntities = syncEntities;
    }

    /**
     * Comprueba si hay entidades en la base de datos central, las actualiza y envia los cambios
     */
    public int syncEntities() {
        log.info("Iniciando la sincronización");
        int errors = 0;
        for (Class<?> entityClass : syncEntities.getEntitiesToSync()) {
            try {
                getRetryer().call(() -> syncEntitiesForClass(entityClass));
            } catch (RetryException e) {
                errors++;
                log.warn("No se pudo sincronizar la entidad " + entityClass, e);
            } catch (ExecutionException e) {
                errors++;
                log.error("No se pudo sincronizar la entidad " + entityClass, e);
            } finally {
                EntitiesHolder.remove();
            }
        }
        log.info("Sincrinización finalizada: " + errors + " entidades con errores");
        return errors;
    }


    Void syncEntitiesForClass(Class<?> entityClass) {
        DbContextHolder.clearDbType();
        AbstractEntityVersion entityVersion = entityVersionRepository.getEntityVersion(entityClass);
        log.trace("Comenzando la sincronización de la entidad " + entityVersion.getEntity().toString());

        log.debug("Buscando entidades modificadas localmente...");
        List dirtyEntities = loadEntities(entityVersion);

        log.debug("Buscando entidades modificadas remotamente...");
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
        String commitVersionField = EntityUtils.getCommitVersionField(entityVersion.getEntity()).getName();
        cq.where(
                cb.or(
                        cb.greaterThan(entity.get(commitVersionField), entityVersion.getUpdateVersion()),
                        cb.isNull(entity.get(commitVersionField))));
        List result = entityManager.createQuery(cq).getResultList();
        log.debug("Se encontraron " + result.size() + " entidad/es");
        return result;
    }

    private <T> void perform(AbstractEntityVersion entityVersion, List<T> dirtyEntities, List<T> remoteEntities) {
        //Filtro las entidades modificadas localmente
        remoteEntities.removeAll(dirtyEntities);

        log.info("Actualizando " + remoteEntities.size() + " entidades modificadas remotamente");
        entityVersion = updateEntities(entityVersion, remoteEntities);

        log.info("Actualizando " + dirtyEntities.size() + " entidades modificadas localmente");
        commitEntities(entityVersion, dirtyEntities);
    }

    @Transactional(rollbackFor = Throwable.class)
    private <T> AbstractEntityVersion updateEntities(AbstractEntityVersion entityVersion, List<T> remoteEntities) {
        EntitySynchronizer<T> synchronizer = getEntitySynchronizer(entityVersion.getEntity());
        log.trace("Utilizando sincronizador: " + synchronizer.getClass().getName());
        Comparator<T> comparator = getEntityComparator(entityVersion.getEntity());
        log.trace("Utilizando comparador: " + comparator.getClass().getName());
        remoteEntities.sort(comparator);
        remoteEntities.forEach(entity -> updateEntity(entity, synchronizer, entityVersion));

        //Igualo las versiones de la entidad
        entityVersion.setCommitVersion(entityVersion.getUpdateVersion());
        return entityVersionRepository.save(entityVersion);
    }

    private <T> void updateEntity(T entity, EntitySynchronizer<T> synchronizer, AbstractEntityVersion entityVersion) {
        log.info("Actualizando entidad: " + entity.toString());
        synchronizer.updateEntity(entity);
        Long commitVersion = EntityUtils.getCommitVersion(entity);
        if (entityVersion.getUpdateVersion() < commitVersion) {
            entityVersion.setUpdateVersion(commitVersion);
        }
    }

    private <T> void commitEntities(AbstractEntityVersion entityVersion, List<T> entities) {
        EntitySynchronizer<T> synchronizer = getEntitySynchronizer(entityVersion.getEntity());
        log.trace("Utilizando sincronizador: " + synchronizer.getClass().getName());
        Comparator<T> comparator = getEntityComparator(entityVersion.getEntity());
        log.trace("Utilizando comparador: " + comparator.getClass().getName());
        entities.sort(comparator);
        entities.forEach(entity -> commitEntity(entity, synchronizer, entityVersion));

        //La siguiente actualización puede fallar por bloqueo optimista,
        //lo cual implica que las entidades se volveran a actualizar la proxima sincronizacion
        entityVersion.setUpdateVersion(entityVersion.getCommitVersion());
        entityVersionRepository.save(entityVersion);
    }

    private <T> void commitEntity(T entity, EntitySynchronizer<T> synchronizer, AbstractEntityVersion entityVersion) {
        log.info("Actualizando entidad: " + entity.toString());
        Long nextVersion = entityVersion.incCommitVersion();
        EntityUtils.setCommitVersion(entity, nextVersion);
        doInCentral(() -> synchronizer.updateEntity(entity));
        synchronizer.updateEntity(entity);
    }

    private <T> EntitySynchronizer<T> getEntitySynchronizer(Class<?> entityClass) {
        //TODO: Crear un provider y cachear sincronizadores
        Sync sync = AnnotationUtils.findAnnotation(entityClass, Sync.class);
        EntitySynchronizer<T> synchronizer = appContext.getBean(sync.synchronizer());
        Preconditions.checkState(synchronizer != null,
                "No existe un sincronizador con el nombre: " + sync.synchronizer().getName());
        return synchronizer;
    }

    private <T> Comparator<T> getEntityComparator(Class<?> entityClass) {
        //TODO: Crear un provider y cachear sincronizadores
        try {
            Sync sync = AnnotationUtils.findAnnotation(entityClass, Sync.class);
            return sync.comparator().newInstance();
        } catch (Exception e) {
            log.warn("No se pudo inicializar el comparador", e);
        }
        return new DummyComparator<T>();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
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

    private Retryer<Void> getRetryer() {
        if (retryer == null) {
            retryer = RetryerBuilder.<Void>newBuilder()
                    .retryIfExceptionOfType(OptimisticLockException.class)
                    .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                    .build();
        }
        return retryer;
    }
}
