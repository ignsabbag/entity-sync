package com.entitysync.config;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Registers beans to support the synchronization of entities
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
class SyncBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes enableSyncEntities = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(
                        EnableSyncEntities.class.getName(), false));

        Set<String> basePackages = new LinkedHashSet<String>();
        String[] basePackagesArray = enableSyncEntities.getStringArray("value");
        basePackages.addAll(getTokenizedBasePackages(basePackagesArray));
        if (basePackages.isEmpty()) {
            basePackagesArray = enableSyncEntities.getStringArray("basePackages");
            basePackages.addAll(getTokenizedBasePackages(basePackagesArray));
            for (Class<?> clazz : enableSyncEntities.getClassArray("basePackageClasses")) {
                basePackages.add(ClassUtils.getPackageName(clazz));
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }

        registerSyncEntitiesBean(basePackages, registry);
    }

    private Set<String> getTokenizedBasePackages(String[] basePackagesArray) {
        Set<String> basePackages = new LinkedHashSet<String>();
        for (String pkg : basePackagesArray) {
            String[] tokenized = StringUtils.tokenizeToStringArray(this.environment.resolvePlaceholders(pkg),
                    ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            basePackages.addAll(Arrays.asList(tokenized));
        }
        return basePackages;
    }

    private void registerSyncEntitiesBean(Set<String> basePackage, BeanDefinitionRegistry registry) {
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

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
