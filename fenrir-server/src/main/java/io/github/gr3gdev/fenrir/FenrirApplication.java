package io.github.gr3gdev.fenrir;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.gr3gdev.fenrir.server.annotation.Listener;
import io.github.gr3gdev.fenrir.server.annotation.Route;
import io.github.gr3gdev.fenrir.server.http.RouteListener;
import io.github.gr3gdev.fenrir.server.plugin.Plugin;
import io.github.gr3gdev.fenrir.server.plugin.impl.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.server.socket.SocketEvent;

public final class FenrirApplication {
    public static void run(Class<?> mainClass) {
        if (mainClass.isAnnotationPresent(FenrirConfiguration.class)) {
            final Instant start = Instant.now();
            final FenrirConfiguration configuration = mainClass.getAnnotation(FenrirConfiguration.class);
            final Map<Class<?>, Plugin<?>> plugins = loadPlugins(configuration);
            final Server server = initServer(configuration);
            final List<Class<?>> routeClasses = parseRoutes(mainClass);
            routeClasses.parallelStream().forEach(routeClass -> connectRoute(plugins, server, routeClass));
            server.run(start);
        } else {
            throw new RuntimeException("The main class must have a FenrirConguration annotation");
        }
    }

    private static Map<Class<?>, Plugin<?>> loadPlugins(final FenrirConfiguration configuration) {
        final Map<Class<?>, Plugin<?>> plugins = Arrays.stream(configuration.plugins())
                .map(FenrirApplication::initPlugin)
                .collect(Collectors.toMap(p -> p.getClass(), Function.identity()));
        plugins.put(FileLoaderPlugin.class, new FileLoaderPlugin());
        return plugins;
    }

    private static Server initServer(final FenrirConfiguration configuration) {
        final Server server = new Server();
        server.setPort(configuration.port());
        return server;
    }

    private static void connectRoute(final Map<Class<?>, Plugin<?>> plugins, final Server server,
            Class<?> routeClass) {
        try {
            final Route route = routeClass.getAnnotation(Route.class);
            final Object routeInstance = routeClass.getDeclaredConstructor().newInstance();
            Arrays.stream(routeClass.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(Listener.class))
                    .forEach(m -> server.add(
                            FenrirApplication.mapMethodToSocketEvent(routeInstance, m,
                                    plugins.get(route.plugin()))));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static SocketEvent mapMethodToSocketEvent(Object instance, Method m, Plugin<?> plugin) {
        final Listener listenerAnnotation = m.getAnnotation(Listener.class);
        final RouteListener routeListener = new RouteListener(
                (req) -> plugin.process(instance, m, req, listenerAnnotation.contentType()));
        return new SocketEvent(listenerAnnotation.path(), listenerAnnotation.method(), routeListener);
    }

    private static Plugin<?> initPlugin(Class<? extends Plugin<?>> pluginClass) {
        try {
            return pluginClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Class<?>> parseRoutes(Class<?> mainClass) {
        final String packageName = mainClass.getPackageName();
        final String packagePath = packageName.replaceAll("[.]", "/");
        try {
            List<Class<?>> classes = Files
                    .walk(Paths.get(ClassLoader.getSystemClassLoader().getResource(packagePath).getPath()))
                    .filter(FenrirApplication::isClass)
                    .map(p -> FenrirApplication.pathToClass(packagePath, p))
                    .filter(FenrirApplication::isRouteAnnotationPresent)
                    .collect(Collectors.toList());
            return classes;
        } catch (IOException e) {
            throw new RuntimeException("Error when parsing classes", e);
        }
    }

    private static boolean isClass(Path path) {
        return path.toFile().isFile() && path.toString().endsWith(".class");
    }

    private static Class<?> pathToClass(String packagePath, Path path) {
        try {
            final String classPath = path.toString().substring(path.toString().indexOf(packagePath),
                    path.toString().lastIndexOf('.'));
            return Class.forName(classPath.replace("/", "."));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isRouteAnnotationPresent(Class<?> routeClass) {
        return routeClass != null && routeClass.isAnnotationPresent(Route.class);
    }
}
