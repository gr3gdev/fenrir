package io.github.gr3gdev.fenrir.validator;

import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpStatus;

/**
 * Json validator : Content-Type is not valid.
 */
public class JsonValidatorException extends ValidatorException {
    public JsonValidatorException(String contentType) {
        super(HttpResponse.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE).content("", contentType));
    }
}
