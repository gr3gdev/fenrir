package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.ErrorListener;
import io.github.gr3gdev.fenrir.Listeners;
import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;
import io.github.gr3gdev.fenrir.socket.WebSocketReader;
import io.github.gr3gdev.fenrir.websocket.WebSocket;
import io.github.gr3gdev.fenrir.websocket.WebSocketListener;
import io.github.gr3gdev.fenrir.websocket.WebSocketRequest;
import io.github.gr3gdev.fenrir.websocket.WebSocketResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Runtime mode for WebSocket API.
 */
public class WebSocketMode implements Mode<WebSocketListener, ErrorListener, WebSocketRequest, WebSocketResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMode.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Listeners<WebSocketRequest, WebSocketListener, ErrorListener> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins, Properties fenrirProperties,
                                                                              List<Interceptor<?, WebSocketResponse, ?>> interceptors) {
        LOGGER.trace("Init WebSocket runtime mode");
        final Map<Class<?>, Object> websockets = parseAndInitWebsockets(mainClass);
        final ConcurrentMap<String, WebSocketListener> listeners = websockets.entrySet().parallelStream()
                .map(entry -> findSocketEvents(plugins, entry.getKey(), entry.getValue(), interceptors))
                .flatMap(e -> e.entrySet().parallelStream())
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        // TODO
        return null;
    }

    private Map<Class<?>, Object> parseAndInitWebsockets(Class<?> mainClass) {
        final WebSocketConfiguration annotation = mainClass.getAnnotation(WebSocketConfiguration.class);
        if (annotation == null) {
            throw new RuntimeException("Missing @WebSocketConfiguration annotation on the main class : " + mainClass.getCanonicalName());
        }
        return Arrays.stream(annotation.websockets())
                .parallel()
                .collect(Collectors.toMap(Function.identity(), ClassUtils::newInstance));
    }

    private ConcurrentMap<String, WebSocketListener> findSocketEvents(Map<Class<?>, Plugin> plugins,
                                                                      Class<?> webSocketClass, Object webSocketInstance,
                                                                      List<Interceptor<?, WebSocketResponse, ?>> interceptors) {
        LOGGER.trace("Find socket events for {}", webSocketClass.getCanonicalName());
        final WebSocket webSocket = webSocketClass.getAnnotation(WebSocket.class);
        if (webSocket == null) {
            throw new RuntimeException("Missing @WebSocket annotation on the class : " + webSocketClass.getCanonicalName());
        }
        return Arrays.stream(webSocketClass.getMethods())
                .filter(m -> m.isAnnotationPresent(WebSocket.Open.class) || m.isAnnotationPresent(WebSocket.Close.class)
                        || m.isAnnotationPresent(WebSocket.Message.class) || m.isAnnotationPresent(WebSocket.Error.class))
                .map(m -> mapMethodToWebSocketEvent(m, webSocketInstance))
                .flatMap(e -> e.entrySet().parallelStream())
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private ConcurrentMap<String, WebSocketListener> mapMethodToWebSocketEvent(Method method, Object webSocketInstance) {
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

    @Override
    public boolean accept(String firstLine) {
        return false;
    }
}
