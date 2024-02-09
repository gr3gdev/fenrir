package io.github.gr3gdev.fenrir.validator;

import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpStatus;

/**
 * Exception when Content-Type is not valid.
 */
public class ContentTypeValidatorException extends ValidatorException {
    public ContentTypeValidatorException(String contentType) {
        super(HttpResponse.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE).content("", contentType));
    }
}
