package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.plugin.Plugin;

import java.util.Map;
import java.util.Set;

/**
 * The runtime mode for the server.
 *
 * @param <S> the SocketEvent implementation used by the mode
 */
public interface Mode<S extends SocketEvent> {
    /**
     * Initialisation of the mode called when the server started.
     *
     * @param mainClass the main class
     * @param plugins   the list of the plugins
     * @return Set of SocketEvent
     */
    Set<S> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins);

    /**
     * @return the class of the SocketReader implementation.
     */
    Class<? extends SocketReader> getSocketReaderClass();
}
