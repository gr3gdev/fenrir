package io.github.gr3gdev.fenrir.http.impl;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.RemoteAddress;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HTTP implementation of {@link io.github.gr3gdev.fenrir.Request}.
 */
public class HttpRequestImpl implements HttpRequest {

    private final String remoteAddress;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();
    private String httpMethod = "";
    private String path = "";
    private String protocol = "";

    /**
     * Constructor.
     *
     * @param remoteAddress the remote address
     * @param input         the input stream of the socket
     */
    @SneakyThrows
    public HttpRequestImpl(String remoteAddress, InputStream input) {
        this.remoteAddress = remoteAddress;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        final String requestLine = reader.readLine();
        if (requestLine != null) {
            final StringTokenizer tokens = new StringTokenizer(requestLine);
            // HTTP Method (GET, POST, ...)
            httpMethod = tokens.nextToken();
            if (tokens.hasMoreTokens()) { // Path
                path = tokens.nextToken();
            }
            if (tokens.hasMoreTokens()) { // Protocol HTTP
                protocol = tokens.nextToken();
            }
        }
        // HEADERS
        ReaderUtil.loadHeaders(this, reader);
        // HTTP PARAMETERS
        String pathParameters = null;
        if (path.contains("?")) {
            pathParameters = path.substring(path.indexOf("?") + 1);
            path = path.substring(0, path.indexOf("?"));
        }
        ReaderUtil.loadParameters(this, pathParameters, reader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String path() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String method() {
        return this.httpMethod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String protocol() {
        return this.protocol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> header(String key) {
        return Optional.ofNullable(this.headers.get(key.toLowerCase()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void headers(String key, String value) {
        this.headers.put(key.toLowerCase(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> headersNames() {
        return this.headers.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> param(String key) {
        return Optional.ofNullable(this.parameters.get(key.toLowerCase()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void params(Stream<String> keys, Consumer<Map<String, String>> action) {
        final Map<String, String> values = keys
                .collect(Collectors.toMap(Function.identity(), k -> param(k).orElse("")));
        action.accept(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void params(String key, String value) {
        this.parameters.put(key.toLowerCase(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> paramsNames() {
        return this.parameters.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteAddress remoteAddress() {
        return new RemoteAddressImpl(this.remoteAddress);
    }

}
