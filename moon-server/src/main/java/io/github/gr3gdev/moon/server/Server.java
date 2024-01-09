package io.github.gr3gdev.moon.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import io.github.gr3gdev.moon.server.http.HttpStatus;
import io.github.gr3gdev.moon.server.http.RequestMethod;
import io.github.gr3gdev.moon.server.http.RouteListener;
import io.github.gr3gdev.moon.server.plugin.ServerPlugin;
import io.github.gr3gdev.moon.server.socket.SocketEvent;
import io.github.gr3gdev.moon.server.socket.SocketReader;

public class Server implements Runnable {

    private final AtomicBoolean active = new AtomicBoolean(false);
    private ServerSocket serverSocket;

    private int port = 9000;

    private final Set<SocketEvent> socketEvents = new HashSet<>();
    private final Set<ServerPlugin> serverPlugins = new HashSet<>();
    private final List<Consumer<Server>> startupEvents = new LinkedList<>();

    public Server() {
        socketEvents.add(
                new SocketEvent(
                        "/favicon.ico", RequestMethod.GET,
                        new RouteListener(HttpStatus.OK, "/favicon.ico", "image/vnd.microsoft.icon")));
    }

    @Override
    public void run() {
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
            System.out.println("MooN Server (" + properties.getProperty("version") + ") started on port " + port);

            startupEvents.forEach(it -> it.accept(this));

            while (active.get()) {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        new Thread(new SocketReader(serverSocket.accept(), socketEvents),
                                "MooN Server SocketReader").start();
                    } catch (IOException exc) {
                        if (active.get() && !(exc instanceof SocketException)) {
                            System.err.println("Server socket error");
                            exc.printStackTrace();
                        }
                    }
                }
            }

            this.serverSocket.close();

            System.out.println("MooN Server stopped");
            clear();
        } catch (IOException e) {
            System.err.println("Initialization error");
            e.printStackTrace();
        }
    }

    public void clear() {
        this.socketEvents.clear();
        this.startupEvents.clear();
        this.serverPlugins.clear();
    }

    public void onStartup(int order, Consumer<Server> event) {
        this.startupEvents.add(order, event);
    }

    /**
     * Process a Request.
     *
     * @param pPath          Path URL
     * @param pRequestMethod Method HTTP
     * @param pRouteListener Route listener
     * @return Server
     */
    public Server process(String pPath, RequestMethod pRequestMethod, RouteListener pRouteListener) {
        pRouteListener.registerPlugins(this.serverPlugins);
        this.socketEvents.add(new SocketEvent(pPath, pRequestMethod, pRouteListener));
        return this;
    }

    /**
     * Add plugin.
     *
     * @param plugin Plugin
     */
    public void plugin(ServerPlugin plugin) {
        this.serverPlugins.add(plugin);
    }
}
