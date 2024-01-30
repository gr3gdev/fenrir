package io.github.gr3gdev.fenrir.security;

import io.github.gr3gdev.fenrir.security.service.SecurityService;
import io.github.gr3gdev.fenrir.security.validator.SecurityValidator;
import io.github.gr3gdev.fenrir.validator.PluginValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration of security.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityConfiguration {
    /**
     * The validator used for security check.
     *
     * @return Class extends {@link PluginValidator}
     */
    Class<? extends SecurityValidator<?>> validator();

    /**
     * The security service.
     *
     * @return Class extends {@link SecurityService}
     */
    Class<? extends SecurityService> service();
}
