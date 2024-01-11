package io.github.gr3gdev.fenrir.event;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.RouteListener;

public interface SocketEvent {
    boolean match(Request request);
    RouteListener getRouteListener();
}
