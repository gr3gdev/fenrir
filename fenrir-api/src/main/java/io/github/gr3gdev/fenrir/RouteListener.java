package io.github.gr3gdev.fenrir;

import java.io.OutputStream;

/**
 * Listener called when a route is used.
 */
public interface RouteListener {
    /**
     * Execute when a request match.
     *
     * @param request the request
     * @param output  the output of the socket (for writing response)
     */
    void handleEvent(Request request, OutputStream output);
}
