package io.github.gr3gdev.fenrir.http.impl;

import io.github.gr3gdev.fenrir.http.ConditionalRequest;
import io.github.gr3gdev.fenrir.http.HttpRequest;

/**
 * None implementation of {@link ConditionalRequest}.
 */
public class NoneConditionalRequest implements ConditionalRequest {
    @Override
    public String findContentType(HttpRequest request) {
        return null;
    }
}
