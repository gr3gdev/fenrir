package io.github.gr3gdev.fenrir;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.runtime.Mode;

public final class FenrirApplication {
    public static void run(Class<?> mainClass) {
        if (mainClass.isAnnotationPresent(FenrirConfiguration.class)) {
            final Instant start = Instant.now();
            final FenrirConfiguration configuration = mainClass.getAnnotation(FenrirConfiguration.class);
            final Map<Class<?>, Plugin<?, ? extends Request, ? extends Response>> plugins = loadPlugins(configuration);
            final Server server = initServer(configuration);
            initModes(configuration.modes(), mainClass, plugins, server);
            server.run(start);
        } else {
            throw new RuntimeException("The main class must have a FenrirConguration annotation");
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void initModes(
            Class<? extends Mode<? extends SocketEvent, ? extends Request, ? extends Response>>[] modes,
            Class<?> mainClass,
            Map<Class<?>, Plugin<?, ? extends Request, ? extends Response>> plugins,
            Server server) {
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

    private static Map<Class<?>, Plugin<?, ? extends Request, ? extends Response>> loadPlugins(
            final FenrirConfiguration configuration) {
        return Arrays.stream(configuration.plugins())
                .map(FenrirApplication::initPlugin)
                .collect(Collectors.toMap(Plugin::getClass, Function.identity()));
    }

    private static Server initServer(final FenrirConfiguration configuration) {
        final Server server = new Server();
        server.setPort(configuration.port());
        return server;
    }

    private static Plugin<?, ? extends Request, ? extends Response> initPlugin(
            Class<? extends Plugin<?, ? extends Request, ? extends Response>> pluginClass) {
        try {
            return pluginClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
