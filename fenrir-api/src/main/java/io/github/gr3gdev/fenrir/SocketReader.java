package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

@RequiredArgsConstructor
public abstract class SocketReader implements Runnable {

    private final Socket socket;
    private final Set<SocketEvent> socketEvents;

    protected abstract Request newRequest(String remoteAddress, InputStream input);

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
