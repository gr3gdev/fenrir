package io.github.gr3gdev.fenrir.event;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.RouteListener;

/**
 * The event used for resolve a socket response.
 */
public interface SocketEvent {
    /**
     * Check if the request can be resolved with this {@link SocketEvent}.
     *
     * @param request the request
     * @return boolean
     */
    boolean match(Request request);

    /**
     * @return the {@link RouteListener} for resolve the response
     */
    RouteListener getRouteListener();
}
