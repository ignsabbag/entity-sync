package com.entitysync.config;

import com.entitysync.annotations.Sync;
import com.entitysync.model.EntityVersionRepository;
import com.entitysync.utils.EntityUtils;
import com.entitysync.utils.SyncComparator;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Finds and holds the list of entities to sync
 *
 * Created by ignsabbag on 08/05/16.
 */
public class SyncEntities {

    private final List<Class<?>> entitiesToSync;

    @Autowired
    public SyncEntities(String basePackage, EntityVersionRepository entityVersionDao) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Sync.class));

        entitiesToSync = Lists.newArrayList();
        entitiesToSync.addAll(scanner.findCandidateComponents(basePackage).stream()
                .map(bd -> EntityUtils.getEntityClass(bd.getBeanClassName()))
                .collect(Collectors.toList()));

        entitiesToSync.sort(new SyncComparator());
//        entitiesToSync.forEach(entityVersionDao::getEntityVersion);
    }

    public List<Class<?>> getEntitiesToSync() {
        return entitiesToSync;
    }
}
