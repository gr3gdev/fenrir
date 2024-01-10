package io.github.gr3gdev.fenrir;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import io.github.gr3gdev.fenrir.server.AddSocketEvent;
import io.github.gr3gdev.fenrir.server.AddStartupEvent;
import io.github.gr3gdev.fenrir.server.StartupEvent;
import io.github.gr3gdev.fenrir.server.http.HttpStatus;
import io.github.gr3gdev.fenrir.server.http.RequestMethod;
import io.github.gr3gdev.fenrir.server.http.Response;
import io.github.gr3gdev.fenrir.server.http.RouteListener;
import io.github.gr3gdev.fenrir.server.socket.SocketEvent;
import io.github.gr3gdev.fenrir.server.socket.SocketReader;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;

@NoArgsConstructor
final class Server {

    private final AtomicBoolean active = new AtomicBoolean(false);
    private ServerSocket serverSocket;

    @Setter
    private int port;

    @Delegate(types = AddSocketEvent.class)
    private final Set<SocketEvent> socketEvents = new HashSet<>();
    @Delegate(types = AddStartupEvent.class)
    private final List<Consumer<StartupEvent>> startupEvents = new LinkedList<>();

    public void run(Instant start) {
        final Response favicon = Response.of(HttpStatus.OK)
                .file("/favicon.ico", "image/vnd.microsoft.icon");
        socketEvents.add(
                new SocketEvent(
                        "/favicon.ico", RequestMethod.GET, new RouteListener((req) -> favicon)));
        try {
            active.set(true);
            final byte[] banner = Objects.requireNonNull(getClass().getResourceAsStream("/banner.txt")).readAllBytes();
            final Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/version.properties"));

            if (serverSocket == null) {
                try {
                    serverSocket = new ServerSocket(port);
                } catch (IOException exc) {
                    throw new RuntimeException(exc);
                }
            }

            System.out.println(new String(banner, StandardCharsets.UTF_8));

            final StartupEvent startupEvent = new StartupEvent();
            startupEvents.forEach(e -> e.accept(startupEvent));
            final long timeInMs = Duration.between(start, Instant.now()).toMillis();
            System.out.println("Fenrir Server (" + properties.getProperty("version") + ") started on port " + port
                    + " in " + timeInMs + "ms");

            while (active.get()) {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        new Thread(new SocketReader(serverSocket.accept(), socketEvents),
                                "Fenrir Server SocketReader").start();
                    } catch (IOException exc) {
                        if (active.get() && !(exc instanceof SocketException)) {
                            System.err.println("Server socket error");
                            exc.printStackTrace();
                        }
                    }
                }
            }

            this.serverSocket.close();

            System.out.println("Fenrir Server stopped");
            this.socketEvents.clear();
            this.startupEvents.clear();
        } catch (IOException e) {
            System.err.println("Initialization error");
            e.printStackTrace();
        }
    }
}
