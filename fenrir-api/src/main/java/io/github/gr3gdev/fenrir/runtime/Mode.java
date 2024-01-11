package io.github.gr3gdev.fenrir.runtime;

import java.util.Map;
import java.util.Set;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.plugin.Plugin;

public interface Mode<S extends SocketEvent, RQ extends Request, RS extends Response> {
    Set<S> init(Class<?> mainClass, Map<Class<?>, Plugin<?, RQ, RS>> plugins);

    Class<? extends SocketReader> getSocketReaderClass();
}
