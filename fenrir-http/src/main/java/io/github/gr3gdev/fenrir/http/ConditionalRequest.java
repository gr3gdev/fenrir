package io.github.gr3gdev.fenrir.http;

/**
 * Interface for return dynamically a content-type.
 */
public interface ConditionalRequest {
    /**
     * Find a content-type dynamically from the request.
     *
     * @param request the HTTP request
     * @return String
     */
    String findContentType(HttpRequest request);
}
