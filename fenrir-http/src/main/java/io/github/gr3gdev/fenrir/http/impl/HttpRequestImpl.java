package io.github.gr3gdev.fenrir.http.impl;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.RemoteAddress;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HTTP implementation of {@link io.github.gr3gdev.fenrir.Request}.
 */
public class HttpRequestImpl implements HttpRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestImpl.class);

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
        LOGGER.trace("New request");
        this.remoteAddress = remoteAddress;
        /*
        final DataInputStream in = new DataInputStream(new BufferedInputStream(input));
        final StringBuilder data = new StringBuilder();
        do {
            data.append((char) in.read());
        } while (in.available() != 0);
         */
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        final String requestLine = reader.readLine();
        if (requestLine != null) {
            LOGGER.trace("> {}", requestLine);
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
        loadHeaders(this, reader);
        // HTTP PARAMETERS
        String pathParameters = null;
        if (path.contains("?")) {
            pathParameters = path.substring(path.indexOf("?") + 1);
            path = path.substring(0, path.indexOf("?"));
        }
        loadParameters(this, pathParameters, reader);
    }

    void loadHeaders(HttpRequest request, BufferedReader pReader) throws IOException {
        LOGGER.trace("Load headers");
        String headerLine = pReader.readLine();
        while (headerLine != null && !headerLine.isBlank()) {
            LOGGER.trace("> {}", headerLine);
            final StringTokenizer hTokens = new StringTokenizer(headerLine, ":");
            if (hTokens.hasMoreTokens()) {
                final String key = hTokens.nextToken();
                if (hTokens.hasMoreTokens()) {
                    request.headers(key, hTokens.nextToken().trim());
                }
            }
            headerLine = pReader.readLine();
        }
    }

    void loadParameters(HttpRequest request, String pathParameters, BufferedReader pReader)
            throws IOException {
        LOGGER.trace("Load parameters");
        final StringBuilder payload = new StringBuilder();
        while (pReader.ready()) {
            payload.append((char) pReader.read());
        }
        if (pathParameters != null) {
            payload.append(pathParameters);
        }
        if (!payload.isEmpty()) {
            LOGGER.trace("> {}", payload);
            final Optional<String> contentType = request.header("Content-Type");
            contentType.ifPresentOrElse(
                    it -> {
                        if (it.startsWith("application/json")) {
                            LOGGER.trace("application/json : set body parameter");
                            request.params("body", payload.toString());
                        }
                        if (it.equals("application/x-www-form-urlencoded")) {
                            LOGGER.trace("application/x-www-form-urlencoded : extract parameters");
                            extractParameters(payload, request);
                        }
                    }, () -> LOGGER.warn("No Content-Type found"));
            if (pathParameters != null) {
                extractParameters(payload, request);
            }
            if (payload.toString().contains("Content-Disposition: form-data;")) {
                LOGGER.warn("multipart/form-data is not implemented !");
            }
        }
    }

    void extractParameters(StringBuilder payload, HttpRequest request) {
        final StringTokenizer pTokens = new StringTokenizer(payload.toString(), "&");
        while (pTokens.hasMoreTokens()) {
            final StringTokenizer vTokens = new StringTokenizer(pTokens.nextToken(), "=");
            if (vTokens.hasMoreTokens()) {
                final String key = vTokens.nextToken();
                if (vTokens.hasMoreTokens()) {
                    request.params(key, vTokens.nextToken());
                }
            }
        }
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
