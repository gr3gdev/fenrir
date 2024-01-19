package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.runtime.Mode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

/**
 * Create a Fenrir application in a main method.
 * <p>
 * Example with fenrir-http :
 * <pre>{@code
 * @FenrirConfiguration(modes = {HttpMode.class})
 * public class MyApp {
 *     public static void main(String[] args) {
 *         FenrirApplication.run(MyApp.class);
 *     }
 * }
 * }</pre>
 */
public final class FenrirApplication {

    private FenrirApplication() {
        // None
    }

    /**
     * Run the server.
     *
     * @param mainClass the main class (used for reflection)
     */
    public static void run(final Class<?> mainClass) {
        if (mainClass.isAnnotationPresent(FenrirConfiguration.class)) {
            try {
                LogManager.getLogManager().readConfiguration(FenrirApplication.class.getResourceAsStream("/logging.properties"));
            } catch (IOException e) {
                throw new RuntimeException("Unable to read logging.properties", e);
            }
            final Instant start = Instant.now();
            final FenrirConfiguration configuration = mainClass.getAnnotation(FenrirConfiguration.class);
            final Map<Class<?>, Plugin> plugins = loadPlugins(configuration, mainClass);
            final Server server = initServer(configuration);
            initModes(configuration.modes(), mainClass, plugins, server);
            server.run(start);
        } else {
            throw new RuntimeException("The main class must have a FenrirConguration annotation");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void initModes(
            final Class<? extends Mode<? extends SocketEvent>>[] modes,
            final Class<?> mainClass, final Map<Class<?>, Plugin> plugins, final Server server) {
        Arrays.stream(modes).parallel()
                .forEach(mode -> {
                    try {
                        final Mode instance = mode.getDeclaredConstructor().newInstance();
                        final Set<? extends SocketEvent> socketEvents = instance.init(mainClass, plugins);
                        server.addEvents(socketEvents, instance.getSocketReaderClass());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                             | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        throw new RuntimeException("Unable to load runtime mode " + mode.getCanonicalName(), e);
                    }
                });
    }

    private static Map<Class<?>, Plugin> loadPlugins(final FenrirConfiguration configuration, final Class<?> mainClass) {
        return Arrays.stream(configuration.plugins())
                .map(pluginClass -> initPlugin(pluginClass, mainClass))
                .collect(Collectors.toMap(Plugin::getClass, Function.identity()));
    }

    private static Server initServer(final FenrirConfiguration configuration) {
        final Server server = new Server();
        server.setPort(configuration.port());
        return server;
    }

    private static Plugin initPlugin(final Class<? extends Plugin> pluginClass, final Class<?> mainClass) {
        try {
            final Plugin plugin = pluginClass.getDeclaredConstructor().newInstance();
            plugin.init(mainClass);
            return plugin;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
