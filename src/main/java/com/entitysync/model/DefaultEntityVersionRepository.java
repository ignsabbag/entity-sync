package com.entitysync.model;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Created by ignsabbag on 09/04/16.
 */
public class DefaultEntityVersionRepository implements EntityVersionRepository {

    private final SessionFactory sessionFactory;
    private final PlatformTransactionManager txManager;
    private final DefaultTransactionDefinition readOnlyTxDefinition;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public DefaultEntityVersionRepository(SessionFactory sessionFactory, PlatformTransactionManager txManager) {
        this.sessionFactory = sessionFactory;
        this.txManager = txManager;
        readOnlyTxDefinition = new DefaultTransactionDefinition();
        readOnlyTxDefinition.setReadOnly(true);
    }

    @Override
    public AbstractEntityVersion findByEntity(Class<?> entityClass) {
        TransactionStatus status = txManager.getTransaction(readOnlyTxDefinition);
        try {
            return (AbstractEntityVersion) sessionFactory.getCurrentSession()
                    .createCriteria(DefaultEntityVersion.class)
                    .add(Restrictions.eq("entity", entityClass))
                    .uniqueResult();
        } finally {
            txManager.rollback(status);
        }
    }

    @Override
    public AbstractEntityVersion newEntityVersion(Class<?> entityClass) {
        return new DefaultEntityVersion(entityClass);
    }

    @Override
    public AbstractEntityVersion save(AbstractEntityVersion entityVersion) {
        TransactionStatus status = txManager.getTransaction(null);
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(entityVersion);
        } catch(Throwable t) {
            txManager.rollback(status);
            throw t;
        }
        txManager.commit(status);
        return entityVersion;
    }

    public void deleteAll() {
        TransactionStatus status = txManager.getTransaction(null);
        try {
            Query query = sessionFactory.getCurrentSession()
                    .createQuery("delete from " + DefaultEntityVersion.class.getName());
            int deletedRows = query.executeUpdate();
            log.info("Deleted Rows: " + deletedRows);
            txManager.commit(status);
        } catch(Throwable t) {
            txManager.rollback(status);
            throw t;
        }
    }

}
