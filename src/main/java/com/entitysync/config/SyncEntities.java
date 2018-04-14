package com.entitysync.config;

import com.entitysync.annotations.Sync;
import com.entitysync.utils.EntityUtils;
import com.entitysync.utils.SyncComparator;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Finds and holds the list of entities to sync
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
public class SyncEntities {

    private final List<Class<?>> entitiesToSync;

    public SyncEntities(Set<String> basePackages) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Sync.class));

        entitiesToSync = Lists.newArrayList();
        for (String basePackage : basePackages) {
            entitiesToSync.addAll(scanner.findCandidateComponents(basePackage).stream()
                    .map(bd -> EntityUtils.getEntityClass(bd.getBeanClassName()))
                    .collect(Collectors.toList()));
        }

        entitiesToSync.sort(new SyncComparator());
        
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("Entities to sync (ordered): " + entitiesToSync);
    }

    public List<Class<?>> getEntitiesToSync() {
        return entitiesToSync;
    }
}
