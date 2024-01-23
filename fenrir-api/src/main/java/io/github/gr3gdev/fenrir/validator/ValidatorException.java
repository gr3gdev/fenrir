package io.github.gr3gdev.fenrir.validator;

import io.github.gr3gdev.fenrir.Response;
import lombok.Getter;

/**
 * Exception thrown when a validator is not valid.
 */
@Getter
public class ValidatorException extends Exception {
    private final Response response;

    public ValidatorException(Response response) {
        super();
        this.response = response;
    }
}
