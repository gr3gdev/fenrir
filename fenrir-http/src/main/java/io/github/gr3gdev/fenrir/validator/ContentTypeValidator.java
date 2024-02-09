package io.github.gr3gdev.fenrir.validator;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpMode;

import java.util.Map;

/**
 * Validator for Content-type.
 */
public class ContentTypeValidator implements PluginValidator<Body> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Body> getSupportedClass() {
        return Body.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Request request, Map<String, Object> properties, Body annotation) throws ValidatorException {
        final HttpRequest httpRequest = (HttpRequest) request;
        final String requestContentType = httpRequest.header("Content-Type").orElse("");
        if (annotation.contentType().equalsIgnoreCase(requestContentType)) {
            return;
        }
        throw new ContentTypeValidatorException((String) properties.get(HttpMode.CONTENT_TYPE));
    }
}
