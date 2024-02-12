package io.github.gr3gdev.fenrir.socket;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.RouteListener;
import io.github.gr3gdev.fenrir.event.SocketEvent;

/**
 * Implementation of {@link SocketEvent} for WebSocket.
 */
public class WebSocketEvent implements SocketEvent {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Request request) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RouteListener getRouteListener() {
        return null;
    }
}
