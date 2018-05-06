package com.entitysync.model;

import com.entitysync.utils.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author Ignacio Sabbag
 * @since 1.0
 */
@Repository
public class SyncEntityRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public <T> List<T> loadEntities(Class<T> entityClass, Long updateVersion) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> entity = cq.from(entityClass);
        String commitVersionField = EntityUtils.getSyncVersionField(entityClass).getName();
        cq.where(
                cb.or(
                        cb.greaterThan(entity.get(commitVersionField), updateVersion),
                        cb.isNull(entity.get(commitVersionField))));
        List<T> result = entityManager.createQuery(cq).getResultList();
        logger.debug(result.size() + " entities found");
        return result;
    }
}
