package com.entitysync.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies the version field or property of an entity class that
 * serves as synchronization version
 *
 * Only a single <code>Version</code> property or field
 * should be used per class.
 *
 * @author Ignacio Sabbag
 * @since 1.0
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface SyncVersion {
}
