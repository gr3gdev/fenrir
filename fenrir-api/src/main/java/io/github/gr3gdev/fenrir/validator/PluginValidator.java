package io.github.gr3gdev.fenrir.validator;

import io.github.gr3gdev.fenrir.Request;

import java.util.Map;

/**
 * Validator only for plugin.
 */
public interface PluginValidator<A> {
    /**
     * Return the annotation class concerned by the validator.
     *
     * @return Class of annotation
     */
    Class<A> getSupportedClass();

    /**
     * Execute the validator.
     *
     * @param request        the request of the socket
     * @param properties     request & response properties
     * @param annotation     the annotation on the parameter to validate
     * @throws ValidatorException throw when not valid
     */
    void validate(Request request, Map<String, Object> properties, A annotation) throws ValidatorException;
}
