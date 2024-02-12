package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.socket.WebSocketEvent;
import io.github.gr3gdev.fenrir.socket.WebSocketReader;
import io.github.gr3gdev.fenrir.websocket.WebSocketResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Runtime mode for WebSocket API.
 */
public class WebSocketMode implements Mode<WebSocketEvent, WebSocketResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMode.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<WebSocketEvent> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins, Properties fenrirProperties,
                                    List<Interceptor<?, WebSocketResponse, ?>> interceptors) {
        LOGGER.trace("Init WebSocket runtime mode");
        // TODO
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends SocketReader> getSocketReaderClass() {
        return WebSocketReader.class;
    }
}
