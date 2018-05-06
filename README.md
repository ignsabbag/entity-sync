# EntitySync

Is a library based on spring and hibernate 4, aimed of ease the synchronization of entities between a local database and a central database.

### Configuration

To start using the library:

- In your configuration class, add **@EnableSyncEntities** annotation, specifying the base package of your entities.

- Use **SyncDataSourceBuilder** to create a DataSource:
    ```
        new SyncDataSourceBuilder()
                .localDataSource()
                    .driverClassName(...)
                    .databaseUrl(...)
                    .username(...)
                    .password(...)
                    .and()
                .centralDataSource()
                    .driverClassName(...)
                    .databaseUrl(...)
                    .username(...)
                    .password(...)
                    .and().build();
    ```
                
- Add the Spring bean SyncInterceptor to jpa properties map with the key "hibernate.ejb.interceptor".

    ```
        @Bean
        @Autowired
        public EntityManagerFactory entityManagerFactory(DataSource dataSource, SyncInterceptor syncInterceptor) {
            ...
            entityManagerFactoryBean.setDataSource(dataSource);
            entityManagerFactoryBean.getJpaPropertyMap().put("hibernate.ejb.naming_strategy", ImprovedNamingStrategy.class);
            entityManagerFactoryBean.getJpaPropertyMap().put("hibernate.ejb.interceptor", syncInterceptor);
            entityManagerFactoryBean.afterPropertiesSet();
            ...
    ```

- To sync a entity, you have to:
  - Add **@Sync** annotation at class level. You must specify a class witch perform de entity update. It must be a spring bean and must implement **EntitySynchronizer**.
  - Add a field/column which stores the synchronization version. That field must be annotated with **@SyncVersion**.
    ```
        @Entity
        @Sync(synchronizer = BrandSynchronizer.class)
        public class Brand {
            ...
            @SyncVersion
            private Long syncVersion;
            ...
    ```
- To starts the syncronization, you can autowire SyncService or SyncEntityService to sync all your entities o one of them
    ```
        syncService.syncEntities()
        
        or
        
        syncEntityService.syncEntity(Brand.class);
    ```
    
EntitySync uses a embedded H2 database in order to store the last version updated from central and the last version committed to central. The EntitySync DB path is "entitySync/data" and is located on the application path.