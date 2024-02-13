package io.github.gr3gdev.fenrir;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * Class used for read a socket {@link InputStream} (Threadable).
 */
@RequiredArgsConstructor
public abstract class SocketReader implements Runnable {
    private final String firstLine;
    private final BufferedReader bufferedReader;
    private final OutputStream outputStream;
    private final SocketAddress remoteAddress;
    private final ConcurrentMap<Request, RouteListener> listeners;
    private final ErrorListener errorListener;
    @Setter
    private Complete completeAction;

    @FunctionalInterface
    interface Complete {
        void execute() throws IOException;
    }

    /**
     * Create a new request.
     *
     * @param firstLine      the fist line of the {@link InputStream} of the socket
     * @param remoteAddress  the remote address (cf. InetSocketAddress#toString)
     * @param bufferedReader the buffered reader of the {@link InputStream} of the socket
     * @return Request
     */
    protected abstract Request newRequest(String firstLine, String remoteAddress, BufferedReader bufferedReader);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // Request
        final Request request = newRequest(firstLine, remoteAddress.toString(), bufferedReader);
        // Search event
        Optional.ofNullable(listeners.get(request))
                .ifPresentOrElse(
                        l -> l.handleEvent(request, outputStream),
                        () -> errorListener.handleEvent(outputStream, "Unable to find a listener for " + request)
                );
        if (completeAction != null) {
            try {
                completeAction.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
