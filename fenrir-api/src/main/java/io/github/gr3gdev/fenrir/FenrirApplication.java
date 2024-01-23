package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.runtime.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FenrirApplication.class);

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
            final Properties fenrirProperties = new Properties();
            try {
                fenrirProperties.load(mainClass.getResourceAsStream("/fenrir.properties"));
            } catch (IOException e) {
                throw new RuntimeException("Configuration file 'fenrir.properties' is not found !", e);
            }
            final Instant start = Instant.now();
            final FenrirConfiguration configuration = mainClass.getAnnotation(FenrirConfiguration.class);
            final Map<Class<?>, Plugin> plugins = loadPlugins(configuration, mainClass, fenrirProperties);
            final Server server = initServer(configuration);
            initModes(configuration.modes(), mainClass, plugins, server, fenrirProperties);
            server.run(start);
        } else {
            throw new RuntimeException("The main class must have a FenrirConguration annotation");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void initModes(
            final Class<? extends Mode<? extends SocketEvent>>[] modes,
            final Class<?> mainClass, final Map<Class<?>, Plugin> plugins, final Server server, final Properties fenrirProperties) {
        LOGGER.trace("Init runtime modes in parallel");
        Arrays.stream(modes).parallel()
                .forEach(mode -> {
                    try {
                        final Mode instance = mode.getDeclaredConstructor().newInstance();
                        final Set<? extends SocketEvent> socketEvents = instance.init(mainClass, plugins, fenrirProperties);
                        server.addEvents(socketEvents, instance.getSocketReaderClass());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                             | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        throw new RuntimeException("Unable to load runtime mode " + mode.getCanonicalName(), e);
                    }
                });
    }

    private static Map<Class<?>, Plugin> loadPlugins(final FenrirConfiguration configuration, final Class<?> mainClass, final Properties fenrirProperties) {
        LOGGER.trace("Load plugins in parallel");
        return Arrays.stream(configuration.plugins()).parallel()
                .map(pluginClass -> initPlugin(pluginClass, mainClass, fenrirProperties))
                .collect(Collectors.toMap(Plugin::getClass, Function.identity()));
    }

    private static Server initServer(final FenrirConfiguration configuration) {
        final Server server = new Server();
        server.setPort(configuration.port());
        return server;
    }

    private static Plugin initPlugin(final Class<? extends Plugin> pluginClass, final Class<?> mainClass, final Properties fenrirProperties) {
        try {
            LOGGER.trace("Init plugin {}", pluginClass.getCanonicalName());
            final Plugin plugin = pluginClass.getDeclaredConstructor().newInstance();
            plugin.init(mainClass, fenrirProperties);
            return plugin;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
