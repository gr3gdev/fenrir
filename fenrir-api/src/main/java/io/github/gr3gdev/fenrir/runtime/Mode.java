package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.*;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The runtime mode for the server.
 *
 * @param <R> the {@link RouteListener} implementation used by the mode
 */
public interface Mode<R extends RouteListener, E extends ErrorListener, RE extends Request, RS extends Response> {
    /**
     * Initialisation of the mode called when the server started.
     *
     * @param mainClass        the main class
     * @param plugins          the list of the plugins
     * @param fenrirProperties fenrir properties
     * @param interceptors     the list of interceptor
     * @return Map of SocketEvent by index
     */
    Listeners<RE, R, E> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins, Properties fenrirProperties,
                            List<Interceptor<?, RS, ?>> interceptors);

    /**
     * @return the class of the SocketReader implementation.
     */
    Class<? extends SocketReader> getSocketReaderClass();

    /**
     * This mode accepts the socket.
     *
     * @param firstLine the socket's first line input
     * @return boolean
     */
    boolean accept(String firstLine);
}
