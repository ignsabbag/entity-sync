package com.entitysync;

import com.entitysync.config.SyncEntities;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import java.util.concurrent.ExecutionException;

/**
 * Service that allows to start entity synchronization
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
@Service
public class SyncService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SyncEntities syncEntities;

    private final SyncEntityService syncEntityService;

    private Retryer<Void> retryer;

    @Autowired
    public SyncService(SyncEntities syncEntities, SyncEntityService syncEntityService) {
        this.syncEntities = syncEntities;
        this.syncEntityService = syncEntityService;
    }

    /**
     * Starts the synchronization for all entities.
     *
     * @return the number of entities that could not be synchronized
     */
    public int syncEntities() {
        logger.info("Starting synchronization..");
        int errors = 0;
        for (Class<?> entityClass : syncEntities.getEntitiesToSync()) {
            try {
                EntitiesHolder.set(entityClass);
                getRetryer().call(() -> syncEntitiesForClass(entityClass));
            } catch (RetryException e) {
                errors++;
                logger.warn("The entity " + entityClass + "could not be synchronized", e);
            } catch (ExecutionException e) {
                errors++;
                logger.error("The entity " + entityClass + "could not be synchronized", e);
            } finally {
                EntitiesHolder.remove();
            }
        }
        logger.info("Synchronization completed: " + errors + " entities with errors");
        return errors;
    }

    private Void syncEntitiesForClass(Class<?> entityClass) {
        syncEntityService.syncEntity(entityClass);
        return null;
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
