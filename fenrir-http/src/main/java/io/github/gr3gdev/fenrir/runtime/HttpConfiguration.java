package io.github.gr3gdev.fenrir.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration of HTTP runtime.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpConfiguration {
    /**
     * List of routes.
     *
     * @return Array of Class
     */
    Class<?>[] routes();
}
