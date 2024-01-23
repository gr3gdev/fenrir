package io.github.gr3gdev.fenrir.validator;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpMode;

import java.util.Map;

/**
 * Validator for {@link JsonPlugin}.
 */
public class JsonValidator implements Validator {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Object... object) {
        return object.length == 1 && object[0] instanceof Body;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Request request, Map<String, Object> properties, Object... object) throws ValidatorException {
        if (object.length == 1) {
            final Body body = (Body) object[0];
            final HttpRequest httpRequest = (HttpRequest) request;
            final String requestContentType = httpRequest.header("Content-Type").orElse("");
            if (body.contentType().equalsIgnoreCase(requestContentType)) {
                return;
            }
        }
        throw new JsonValidatorException((String) properties.getOrDefault(HttpMode.CONTENT_TYPE, "application/json"));
    }
}
