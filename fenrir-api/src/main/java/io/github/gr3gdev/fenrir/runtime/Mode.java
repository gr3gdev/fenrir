package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.plugin.Plugin;

import java.util.Map;
import java.util.Set;

public interface Mode<S extends SocketEvent> {
    Set<S> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins);

    Class<? extends SocketReader> getSocketReaderClass();
}
