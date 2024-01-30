package io.github.gr3gdev.fenrir.security.validator;

import io.github.gr3gdev.fenrir.security.Secure;
import io.github.gr3gdev.fenrir.security.service.SecurityService;
import io.github.gr3gdev.fenrir.validator.PluginValidator;

/**
 * Interface of validator for a security implementation.
 *
 * @param <S> {@link SecurityService}
 */
public interface SecurityValidator<S extends SecurityService> extends PluginValidator<Secure> {
    /**
     * {@inheritDoc}
     */
    default Class<Secure> getSupportedClass() {
        return Secure.class;
    }

    /**
     * Define the {@link SecurityService} implementation.
     *
     * @param service service extends {@link SecurityService}
     */
    void setSecurityService(S service);
}
