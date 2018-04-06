package com.entitysync.config;

import com.entitysync.SyncInterceptor;
import com.entitysync.SyncService;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Created by ignsabbag on 11/03/17.
 */
class SyncBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes =
                AnnotationAttributes.fromMap(
                        importingClassMetadata.getAnnotationAttributes(EnableSyncEntities.class.getName(), false));
        String basePackage = attributes.getString("value");
        registerSyncEntitiesBean(basePackage, registry);
        registerSyncServiceBean(registry);
        registerSyncInterceptorBean(registry);
    }

    private void registerSyncEntitiesBean(String basePackage, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition("syncEntities")) {
            ConstructorArgumentValues cav = new ConstructorArgumentValues();
            cav.addGenericArgumentValue(basePackage);

            RootBeanDefinition gbd = new RootBeanDefinition(SyncEntities.class);
            gbd.setRole(AbstractBeanDefinition.ROLE_SUPPORT);
            gbd.setConstructorArgumentValues(cav);
            gbd.setLazyInit(false);
            gbd.setAutowireCandidate(true);

            registry.registerBeanDefinition("syncEntities", gbd);
        }
    }

    private void registerSyncServiceBean(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition("syncService")) {
            RootBeanDefinition gbd = new RootBeanDefinition(SyncService.class);
            gbd.setRole(AbstractBeanDefinition.ROLE_APPLICATION);
            gbd.setLazyInit(false);
            gbd.setAutowireCandidate(true);
            gbd.setScope("singleton");
            registry.registerBeanDefinition("syncService", gbd);
        }
    }

    private void registerSyncInterceptorBean(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition("syncInterceptor")) {
            RootBeanDefinition gbd = new RootBeanDefinition(SyncInterceptor.class);
            gbd.setRole(AbstractBeanDefinition.ROLE_SUPPORT);
            gbd.setLazyInit(false);
            gbd.setAutowireCandidate(true);
            gbd.setScope("singleton");
            registry.registerBeanDefinition("syncInterceptor", gbd);
        }
    }
}
