package io.github.gr3gdev.fenrir.validator;

import io.github.gr3gdev.fenrir.Request;

import java.util.Map;

/**
 * Validator only for route.
 */
public interface RouteValidator {

    /**
     * Check if the object is concerned by the validator.
     *
     * @param object the object(s) to validate
     * @return boolean
     */
    boolean supports(Object... object);

    /**
     * Execute the validator.
     *
     * @param request    the request of the socket
     * @param properties request & response properties
     * @param object     the object(s) to validate
     * @throws ValidatorException throw when not valid
     */
    void validate(Request request, Map<String, Object> properties, Object... object) throws ValidatorException;

}
