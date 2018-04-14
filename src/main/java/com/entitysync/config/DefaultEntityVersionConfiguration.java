package com.entitysync.config;

import com.entitysync.model.DefaultEntityVersion;
import com.entitysync.model.DefaultEntityVersionRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Provides the configuration of the default implementation
 * for entity <code>EntityVersion</code>.
 *
 * The default configuration consist of a H2 embedded database
 * using Hibernate SessionFactory
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
@Configuration
public class DefaultEntityVersionConfiguration {

    @Value("${entitysync.db.path:./entitySync/data}")
    private String dbPath;

    @Bean
    public SessionFactory sessionFactory() {
        final LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource());
        sessionBuilder.addAnnotatedClasses(DefaultEntityVersion.class);
        sessionBuilder.addProperties(hibernateProperties());
        return sessionBuilder.buildSessionFactory();
    }

    private DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:" + dbPath);
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");

        return dataSource;
    }

    private Properties hibernateProperties() {
        final Properties properties = new Properties();
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create");
        return properties;
    }

    @Bean
    @Autowired
    public DefaultEntityVersionRepository entityVersionRepository(SessionFactory sessionFactory) {
        return new DefaultEntityVersionRepository(sessionFactory, txManager(sessionFactory));
    }

    private HibernateTransactionManager txManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }

}
