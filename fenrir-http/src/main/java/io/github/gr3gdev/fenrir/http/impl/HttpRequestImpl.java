package io.github.gr3gdev.fenrir.http.impl;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.RemoteAddress;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HTTP implementation of {@link io.github.gr3gdev.fenrir.Request}.
 */
public class HttpRequestImpl implements HttpRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestImpl.class);
    private static final Pattern PATH_PARAMETER_PATTERN = Pattern.compile("\\{.*}");

    private String remoteAddress;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> parameters = new HashMap<>();
    private String httpMethod = "";
    private String path = "";
    private String protocol = "";
    private final Map<Integer, String> potentialParams = new HashMap<>();

    /**
     * Constructor.
     *
     * @param method the HTTP method
     * @param path   the request path
     */
    public HttpRequestImpl(String method, String path) {
        this.httpMethod = method;
        this.path = path;
        loadPotentialParameters();
    }

    /**
     * Constructor.
     *
     * @param firstLine     the first line of the input stream of the socket
     * @param remoteAddress the remote address
     * @param reader        the buffered reader of the input stream of the socket
     */
    @SneakyThrows
    public HttpRequestImpl(String firstLine, String remoteAddress, BufferedReader reader) {
        LOGGER.trace("New HTTP request");
        this.remoteAddress = remoteAddress;
        if (firstLine != null) {
            LOGGER.trace("> {}", firstLine);
            final StringTokenizer tokens = new StringTokenizer(firstLine);
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
        loadPotentialParameters();
    }

    void loadPotentialParameters() {
        final String[] params = path.split("/");
        for (int idx = 0; idx < params.length; idx++) {
            if (!params[idx].isBlank()) {
                potentialParams.put(idx, params[idx]);
            }
        }
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
                        if (it.equals("application/x-www-form-urlencoded")) {
                            LOGGER.trace("application/x-www-form-urlencoded : extract parameters");
                            extractParameters(payload, request);
                        } else {
                            // By default, set the payload into body's parameter
                            LOGGER.trace("{} : set body parameter", it);
                            request.params("body", payload.toString());
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
    public Map<String, String> headers() {
        return this.headers;
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
    public Map<String, String> params() {
        return parameters;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, potentialParams.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        final HttpRequestImpl other = (HttpRequestImpl) object;
        return Objects.equals(httpMethod, other.httpMethod) && matchPath(other);
    }

    private boolean matchPath(HttpRequestImpl other) {
        if (potentialParams.size() == other.potentialParams.size()) {
            return other.potentialParams.entrySet().stream()
                    .allMatch(e -> {
                        final int index = e.getKey();
                        final String key = e.getValue();
                        if (Objects.equals(potentialParams.get(index), key)) {
                            return true;
                        } else {
                            final Matcher matcher = PATH_PARAMETER_PATTERN.matcher(key);
                            if (matcher.find()) {
                                // Add path parameters
                                final String param = key.substring(matcher.start() + 1, matcher.end() - 1);
                                LOGGER.trace("Add path parameters : {}", param);
                                other.parameters.put(param, potentialParams.get(index));
                                parameters.put(param, potentialParams.get(index));
                                return true;
                            }
                            return false;
                        }
                    });
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "HttpRequest{" +
                "protocol='" + protocol + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
