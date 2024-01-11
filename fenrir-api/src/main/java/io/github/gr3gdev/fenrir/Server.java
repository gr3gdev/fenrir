package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.event.StartupEvent;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
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

@NoArgsConstructor
final class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final AtomicBoolean active = new AtomicBoolean(false);
    private ServerSocket serverSocket;

    @Setter
    private int port;

    private Set<? extends SocketEvent> socketEvents = new HashSet<>();
    private Class<? extends SocketReader> socketReaderClass;

    @Delegate(types = AddStartupEvent.class)
    private final List<Consumer<StartupEvent>> startupEvents = new LinkedList<>();

    public void run(Instant start) {
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

            LOGGER.info(new String(banner, StandardCharsets.UTF_8));

            final StartupEvent startupEvent = new StartupEvent();
            startupEvents.forEach(e -> e.accept(startupEvent));
            final long timeInMs = Duration.between(start, Instant.now()).toMillis();
            LOGGER.info("Fenrir Server (" + properties.getProperty("version") + ") started on port " + port
                    + " in " + timeInMs + "ms");

            while (active.get()) {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        Thread.ofPlatform().name("Fenrir Server SocketReader")
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

            LOGGER.info("Fenrir Server stopped");
            this.socketEvents.clear();
            this.startupEvents.clear();
        } catch (IOException exc) {
            LOGGER.error("Initialization error", exc);
        }
    }

    public void addEvents(Set<? extends SocketEvent> socketEvents, Class<? extends SocketReader> socketReaderClass) {
        this.socketEvents = socketEvents;
        this.socketReaderClass = socketReaderClass;
    }
}