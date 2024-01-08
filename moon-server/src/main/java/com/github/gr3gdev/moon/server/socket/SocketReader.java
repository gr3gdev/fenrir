package com.github.gr3gdev.moon.server.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

import com.github.gr3gdev.moon.server.http.Request;
import com.github.gr3gdev.moon.server.http.impl.RequestImpl;

public class SocketReader implements Runnable {

    private final Socket socket;
    private final Set<SocketEvent> socketEvents;

    public SocketReader(Socket socket, Set<SocketEvent> socketEvents) {
        this.socket = socket;
        this.socketEvents = socketEvents;
    }

    @Override
    public void run() {
        // Request
        try (final InputStream inputStream = socket.getInputStream();
                final OutputStream outputStream = socket.getOutputStream()) {
            final Request request = new RequestImpl(socket.getRemoteSocketAddress().toString(), inputStream);
            // Search event
            socketEvents.stream()
                    .filter(e -> e.match(request))
                    .forEach(e -> e.getRouteListener().handleEvent(request, outputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
