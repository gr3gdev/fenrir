package io.github.gr3gdev.fenrir;

import java.io.OutputStream;

/**
 * Listener called when an error is returned.
 */
public interface ErrorListener {
    /**
     * Execute when a request match.
     *
     * @param output  the output of the socket (for writing response)
     * @param message the error's message
     */
    void handleEvent(OutputStream output, String message);
}
