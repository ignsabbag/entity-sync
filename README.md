# EntitySync

Is a library based on spring and hibernate 4, aimed of ease the synchronization of entities between a local database and a central database.

### Configuration

To start using the library:

- In your configuration class, add **@EnableSyncEntities** annotation, specifying the base package, where the entities are.
- Add this property to the jpa properties map:
    - Key: "hibernate.ejb.interceptor"
    - Value: "com.entitysync.SyncInterceptor"
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

Also, you need provide a entity witch extends **AbstractEntityVersion** and a repository that implements **EntityVersionRepository**.

To sync a entity, add **@Sync** annotation. You must specify a class witch perform de entity update. It must be a spring bean and must implement **EntitySynchronizer**.