package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.event.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Internal server.
 */
final class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final AtomicBoolean active = new AtomicBoolean(false);
    private final ServerSocket serverSocket;

    private Set<? extends SocketEvent> socketEvents = new HashSet<>();
    private Class<? extends SocketReader> socketReaderClass;

    Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    void run(Instant start, Consumer<StartupEvent>[] startupListeners) {
        try {
            final byte[] banner = Objects.requireNonNull(getClass().getResourceAsStream("/banner.txt")).readAllBytes();
            final Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/version.properties"));
            LOGGER.info(new String(banner, StandardCharsets.UTF_8));

            final String timeInMs = String.valueOf(Duration.between(start, Instant.now()).toMillis() / 1000f);
            LOGGER.info("Server ({}) started on port {} in {} seconds",
                    properties.getProperty("version"), serverSocket.getLocalPort(), timeInMs);

            if (startupListeners != null) {
                Arrays.stream(startupListeners).forEach(s -> s.accept(new StartupEvent(serverSocket.getLocalPort(), active)));
            }

            this.active.set(true);
            while (active.get()) {
                if (!serverSocket.isClosed()) {
                    try {
                        Thread.ofPlatform().name("Fenrir.Server SocketReader")
                                .start(socketReaderClass.getDeclaredConstructor(Socket.class, Set.class)
                                        .newInstance(serverSocket.accept(), socketEvents));
                    } catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException
                             | InvocationTargetException | NoSuchMethodException | SecurityException exc) {
                        if (active.get() && !(exc instanceof SocketException)) {
                            LOGGER.error("Server socket error", exc);
                        }
                    }
                }
            }

            this.serverSocket.close();
            LOGGER.info("Server stopped");

            this.socketEvents.clear();
        } catch (IOException exc) {
            LOGGER.error("Initialization error", exc);
        }
    }

    void addEvents(Set<? extends SocketEvent> socketEvents, Class<? extends SocketReader> socketReaderClass) {
        this.socketEvents = socketEvents;
        this.socketReaderClass = socketReaderClass;
    }
}
