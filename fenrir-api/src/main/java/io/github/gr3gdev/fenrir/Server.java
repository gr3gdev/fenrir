package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.StartupEvent;
import io.github.gr3gdev.fenrir.runtime.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Internal server.
 */
final class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final AtomicBoolean active = new AtomicBoolean(false);
    private final ServerSocket serverSocket;

    private final ConcurrentMap<Mode<? extends RouteListener, ? extends ErrorListener, ? extends Request, ? extends Response>,
            Listeners<? extends Request, ? extends RouteListener, ? extends ErrorListener>> modes = new ConcurrentHashMap<>();

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
                    final Socket socket = serverSocket.accept();
                    final SocketAddress remoteAddress = socket.getRemoteSocketAddress();
                    final InputStream inputstream = socket.getInputStream();
                    final OutputStream outputstream = socket.getOutputStream();
                    try {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
                        final String firstLine = reader.readLine();
                        // Multiple mode support
                        modes.entrySet().stream()
                                .filter(e -> firstLine != null && e.getKey().accept(firstLine))
                                .findFirst()
                                .ifPresentOrElse(entry -> {
                                    // If the mode accept the request
                                    executeMode(entry, firstLine, reader, outputstream, remoteAddress, inputstream);
                                }, () -> {
                                    try {
                                        outputstream.flush();
                                        inputstream.close();
                                        outputstream.close();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    } catch (Exception exc) {
                        if (active.get() && !(exc instanceof SocketException)) {
                            LOGGER.error("Server socket error", exc);
                        }
                    }
                }
            }

            this.serverSocket.close();
            LOGGER.info("Server stopped");

            this.modes.clear();
        } catch (IOException exc) {
            LOGGER.error("Initialization error", exc);
        }
    }

    private static void executeMode(Map.Entry<Mode<? extends RouteListener, ? extends ErrorListener, ? extends Request, ? extends Response>,
            Listeners<? extends Request, ? extends RouteListener, ? extends ErrorListener>> entry,
                                    String firstLine, BufferedReader reader, OutputStream outputstream,
                                    SocketAddress remoteAddress, InputStream inputstream) {
        final Mode<? extends RouteListener, ? extends ErrorListener, ? extends Request, ? extends Response> mode = entry.getKey();
        final Class<? extends SocketReader> socketReaderClass = mode.getSocketReaderClass();
        final Listeners<? extends Request, ? extends RouteListener, ? extends ErrorListener> listeners = entry.getValue();
        try {
            // Find and execute the listener
            final SocketReader socketReader = socketReaderClass.getDeclaredConstructor(String.class, BufferedReader.class, OutputStream.class,
                            SocketAddress.class, ConcurrentMap.class, ErrorListener.class)
                    .newInstance(firstLine, reader, outputstream, remoteAddress,
                            listeners.listeners(), listeners.errorListener());
            socketReader.setCompleteAction(() -> {
                outputstream.flush();
                inputstream.close();
                outputstream.close();
            });
            Thread.ofPlatform().name("Fenrir.Server " + socketReaderClass.getSimpleName())
                    .start(socketReader);
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    void addListeners(Mode<? extends RouteListener, ? extends ErrorListener, ? extends Response, ? extends Request> mode,
                      Listeners<? extends Request, ? extends RouteListener, ? extends ErrorListener> listeners) {
        this.modes.put(mode, listeners);
    }
}
