package com.entitysync;

import com.entitysync.config.EnableSyncEntities;
import com.entitysync.db.builder.SyncDataSourceBuilder;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Created by ignsabbag on 11/03/17.
 */
@Configuration
@EnableSyncEntities
@PropertySource("application.properties")
@ComponentScan("com.entitysync.data")
@EnableJpaRepositories("com.entitysync.data")
public class TestConfiguration {

    @Bean
    public DataSource dataSource() {
        return new SyncDataSourceBuilder()
                .localDataSource()
                    .driverClassName("org.h2.Driver")
                    .databaseUrl("jdbc:h2:./target/data/local;IGNORECASE=TRUE;INIT=RUNSCRIPT FROM './src/test/resources/test_db.sql'")
                    .username("sa")
                    .password("sa")
                    .and()
                .centralDataSource()
                    .driverClassName("org.h2.Driver")
                    .databaseUrl("jdbc:h2:./target/data/central;IGNORECASE=TRUE;INIT=RUNSCRIPT FROM './src/test/resources/test_db.sql'")
                    .username("sa")
                    .password("sa")
                    .and().build();
    }

    @Bean
    @Autowired
    public EntityManagerFactory entityManagerFactory(DataSource dataSource, SyncInterceptor syncInterceptor) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        //explicit set of Persistence provider to avoid a warning because of the deprecated default provider.
        factory.setPersistenceProvider(new HibernatePersistenceProvider());
        factory.setJpaVendorAdapter(jpaVendorAdapter());
        factory.setPackagesToScan("com.entitysync.data");
        factory.setDataSource(dataSource);
        factory.getJpaPropertyMap().put("hibernate.ejb.interceptor", syncInterceptor);
//        factory.getJpaPropertyMap().put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
//        factory.getJpaPropertyMap().put("javax.persistence.schema-generation.scripts.create-target", "db-schema.jpa.ddl");
//        factory.getJpaPropertyMap().put("javax.persistence.schema-generation.scripts.drop-target", "db-schema.jpa.ddl");
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        vendorAdapter.setGenerateDdl(false);
        vendorAdapter.setShowSql(false);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");

        return vendorAdapter;
    }

    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

/*    @Bean //For Spring 3.X
    public HibernateExceptionTranslator hibernateExceptionTranslator(){
        return new HibernateExceptionTranslator();
    }*/
}
