package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

/**
 * Class used for read a socket {@link InputStream} (Threadable).
 */
@RequiredArgsConstructor
public abstract class SocketReader implements Runnable {

    private final Socket socket;
    private final Set<SocketEvent> socketEvents;

    /**
     * Create a new request.
     *
     * @param remoteAddress the remote address (cf. InetSocketAddress#toString)
     * @param input         the {@link InputStream} of the socket
     * @return Request
     */
    protected abstract Request newRequest(String remoteAddress, InputStream input);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // Request
        try (final InputStream inputStream = socket.getInputStream();
             final OutputStream outputStream = socket.getOutputStream()) {
            final Request request = newRequest(socket.getRemoteSocketAddress().toString(), inputStream);
            // Search event
            socketEvents.stream()
                    .filter(e -> e.match(request))
                    .forEach(e -> e.getRouteListener().handleEvent(request, outputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
