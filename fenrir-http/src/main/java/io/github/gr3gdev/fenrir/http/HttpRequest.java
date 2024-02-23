package io.github.gr3gdev.fenrir.http;

import io.github.gr3gdev.fenrir.Request;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Interface for HTTP request.
 */
public interface HttpRequest extends Request {

    /**
     * Path URL.
     *
     * @return String
     */
    String path();

    /**
     * HTTP Method.
     *
     * @return String
     */
    String method();

    /**
     * HTTP Protocol.
     *
     * @return String
     */
    String protocol();

    /**
     * Get HTTP Request header value.
     *
     * @return Optional of String
     */
    Optional<String> header(String key);

    /**
     * Get HTTP Request headers.
     *
     * @return Map String of String
     */
    Map<String, String> headers();

    /**
     * Put HTTP Request Headers.
     *
     * @param key   the key to add
     * @param value the value to add
     */
    void headers(String key, String value);

    /**
     * Get HTTP Request Headers names.
     *
     * @return Set of String
     */
    Set<String> headersNames();

    /**
     * Execute a consumer action.
     *
     * @param keys   parameter keys
     * @param action action to execute with parameters
     */
    void params(Stream<String> keys, Consumer<Map<String, String>> action);

    /**
     * Put HTTP Request parameters.
     *
     * @param key   the key to add
     * @param value the value to add
     */
    void params(String key, String value);

    /**
     * Get HTTP Request parameters.
     *
     * @return Map String of String
     */
    Map<String, String> params();

    /**
     * Get HTTP Request parameters names.
     *
     * @return Set of String
     */
    Set<String> paramsNames();

    /**
     * Client remote address.
     *
     * @return RemoteAddress
     */
    RemoteAddress remoteAddress();
}
