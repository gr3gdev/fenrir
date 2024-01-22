package io.github.gr3gdev.fenrir.jpa.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration of JPA plugin.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaConfiguration {
    /**
     * List of entities.
     *
     * @return Array of Class
     */
    Class<?>[] entitiesClass();
}
