package com.entitysync.annotations;

import com.entitysync.EntitySynchronizer;
import com.entitysync.utils.DummyComparator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a entity as to be synced
 *
 * Created by ignsabbag on 08/05/16.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Sync {

    /**
     * The synchronizer class witch perform de entity update
     */
    Class<? extends EntitySynchronizer> synchronizer();

    /**
     * Classes that must be synced before the annotated entity
     */
    Class<?>[] dependsOn() default {};

    /**
     * Comparator that will be used to sort the list of entities to sync
     */
    Class<? extends Comparator> comparator() default DummyComparator.class;
}
