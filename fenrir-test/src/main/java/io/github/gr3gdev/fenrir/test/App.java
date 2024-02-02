package io.github.gr3gdev.fenrir.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation on a field of a class which start a {@link io.github.gr3gdev.fenrir.FenrirApplication}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface App {
    /**
     * The class with the {@link io.github.gr3gdev.fenrir.FenrirConfiguration}.
     *
     * @return Class
     */
    Class<?> value();

    /**
     * Timeout for server start.
     *
     * @return long
     */
    long timeoutInSeconds() default 5;
}
