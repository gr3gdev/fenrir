package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.properties.FenrirProperties;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;
import io.github.gr3gdev.fenrir.runtime.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
    @SuppressWarnings("rawtypes")
    public static void run(final Class<?> mainClass) {
        if (mainClass.isAnnotationPresent(FenrirConfiguration.class)) {
            LOGGER.trace("Starting...");
            final Instant start = Instant.now();
            final FenrirConfiguration annotation = mainClass.getAnnotation(FenrirConfiguration.class);
            LOGGER.trace("Read configuration");
            final FenrirConfigurationInternal configuration = new FenrirConfigurationInternal(annotation);
            final FenrirProperties fenrirProperties = new FenrirProperties();
            try (InputStream stream = mainClass.getResourceAsStream("/fenrir.properties")) {
                if (stream != null) {
                    fenrirProperties.load(stream);
                }
            } catch (IOException e) {
                throw new RuntimeException("Configuration file 'fenrir.properties' is not found !", e);
            }
            LOGGER.trace("Init interceptors...");
            final List<Interceptor> interceptors = configuration.getInterceptors().stream()
                    .map(i -> (Interceptor) ClassUtils.newInstance(i))
                    .toList();
            LOGGER.trace("Init plugins...");
            final Map<Class<?>, Plugin> plugins = loadPlugins(configuration, mainClass, fenrirProperties);
            LOGGER.trace("Init server...");
            final Server server = initServer(configuration);
            LOGGER.trace("Init modes...");
            initModes(configuration, mainClass, plugins, server, fenrirProperties, interceptors);
            server.run(start);
        } else {
            throw new RuntimeException("The main class must have a @FenrirConguration annotation");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void initModes(
            final FenrirConfigurationInternal configuration,
            final Class<?> mainClass, final Map<Class<?>, Plugin> plugins, final Server server,
            final FenrirProperties fenrirProperties, final List<Interceptor> interceptors) {
        configuration.getModes().parallelStream()
                .forEach(mode -> {
                    try {
                        final Mode instance = mode.getDeclaredConstructor().newInstance();
                        LOGGER.trace("Init mode {}", instance.getClass().getCanonicalName());
                        final Set<? extends SocketEvent> socketEvents = instance.init(mainClass, plugins, fenrirProperties, interceptors);
                        server.addEvents(socketEvents, instance.getSocketReaderClass());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                             | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        throw new RuntimeException("Unable to load runtime mode " + mode.getCanonicalName(), e);
                    }
                });
    }

    private static Map<Class<?>, Plugin> loadPlugins(final FenrirConfigurationInternal configuration, final Class<?> mainClass,
                                                     final FenrirProperties fenrirProperties) {
        return configuration.getPlugins().parallelStream()
                .map(pluginClass -> initPlugin(pluginClass, mainClass, fenrirProperties))
                .collect(Collectors.toMap(Plugin::getClass, Function.identity()));
    }

    private static Server initServer(final FenrirConfigurationInternal configuration) {
        final Server server = new Server();
        server.setPort(configuration.getPort());
        return server;
    }

    private static Plugin initPlugin(final Class<? extends Plugin> pluginClass, final Class<?> mainClass,
                                     final FenrirProperties fenrirProperties) {
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
