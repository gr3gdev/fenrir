package io.github.gr3gdev.fenrir.websocket;

import io.github.gr3gdev.fenrir.Request;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.StringTokenizer;

@Getter
public class WebSocketRequest implements Request {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketRequest.class);

    private String method;
    private String webSocketKey;

    /**
     * Constructor for a WebSocket Request.
     *
     * @param remoteAddress the remote address (host:port)
     * @param input         the inputstream receive by the socket
     */
    @SneakyThrows
    public WebSocketRequest(String remoteAddress, InputStream input) {
        LOGGER.trace("New WebSocket request");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        final String requestLine = reader.readLine();
        if (requestLine != null) {
            LOGGER.trace("> {}", requestLine);
            final StringTokenizer tokens = new StringTokenizer(requestLine);
            method = tokens.nextToken();
            while (tokens.hasMoreTokens()) {
                // TODO
                LOGGER.info(tokens.nextToken());
            }
        }
    }

    @Override
    public Optional<String> param(String key) {
        return Optional.empty();
    }
}
