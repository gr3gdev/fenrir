package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * The runtime mode for the server.
 *
 * @param <S> the SocketEvent implementation used by the mode
 */
public interface Mode<S extends SocketEvent, RS extends Response> {
    /**
     * Initialisation of the mode called when the server started.
     *
     * @param mainClass        the main class
     * @param plugins          the list of the plugins
     * @param fenrirProperties fenrir properties
     * @param interceptors     the list of interceptor
     * @return Set of SocketEvent
     */
    Set<S> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins, Properties fenrirProperties, List<Interceptor<?, RS, ?>> interceptors);

    /**
     * @return the class of the SocketReader implementation.
     */
    Class<? extends SocketReader> getSocketReaderClass();
}
