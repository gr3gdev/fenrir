package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.*;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.plugin.impl.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.socket.HttpSocketEvent;
import io.github.gr3gdev.fenrir.socket.HttpSocketReader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpMode implements Mode<HttpSocketEvent, HttpRequest, HttpResponse> {

    @Override
    public Set<HttpSocketEvent> init(Class<?> mainClass, Map<Class<?>, Plugin<?, HttpRequest, HttpResponse>> plugins) {
        plugins.put(FileLoaderPlugin.class, new FileLoaderPlugin());
        final List<Class<?>> routes = parseRoutes(mainClass);
        final Set<HttpSocketEvent> socketEvents = new HashSet<>();
        final HttpResponse favicon = HttpResponse.of(HttpStatus.OK)
                .file("/favicon.ico", "image/vnd.microsoft.icon");
        socketEvents.add(new HttpSocketEvent(
                "/favicon.ico", HttpMethod.GET, new HttpRouteListener((req) -> favicon)));
        socketEvents.addAll(routes.stream()
                .map(routeClass -> findSocketEvents(plugins, routeClass))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
        return socketEvents;
    }

    private boolean isClass(Path path) {
        return path.toFile().isFile() && path.toString().endsWith(".class");
    }

    private Class<?> pathToClass(String packagePath, Path path) {
        try {
            final String classPath = path.toString().substring(path.toString().indexOf(packagePath),
                    path.toString().lastIndexOf('.'));
            return Class.forName(classPath.replace("/", "."));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isRouteAnnotationPresent(Class<?> routeClass) {
        return routeClass != null && routeClass.isAnnotationPresent(Route.class);
    }

    private List<Class<?>> parseRoutes(Class<?> mainClass) {
        final String packageName = mainClass.getPackageName();
        final String packagePath = packageName.replaceAll("[.]", "/");
        try (final Stream<Path> stream = Files.walk(Paths.get(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(packagePath)).getPath()))) {
            return stream
                    .filter(this::isClass)
                    .map(p -> pathToClass(packagePath, p))
                    .filter(this::isRouteAnnotationPresent)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error when parsing classes", e);
        }
    }

    private Set<HttpSocketEvent> findSocketEvents(final Map<Class<?>, Plugin<?, HttpRequest, HttpResponse>> plugins,
                                                  Class<?> routeClass) {
        try {
            final Route route = routeClass.getAnnotation(Route.class);
            final Object routeInstance = routeClass.getDeclaredConstructor().newInstance();
            return Arrays.stream(routeClass.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(Listener.class))
                    .map(m -> mapMethodToSocketEvent(routeInstance, m, plugins.get(route.plugin())))
                    .collect(Collectors.toSet());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                 | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpSocketEvent mapMethodToSocketEvent(Object instance, Method m,
                                                   Plugin<?, HttpRequest, HttpResponse> plugin) {
        final Listener listenerAnnotation = m.getAnnotation(Listener.class);
        final HttpRouteListener routeListener = new HttpRouteListener(
                (req) -> plugin.process(instance, m, req, listenerAnnotation.contentType()));
        return new HttpSocketEvent(listenerAnnotation.path(), listenerAnnotation.method(), routeListener);
    }

    @Override
    public Class<? extends SocketReader> getSocketReaderClass() {
        return HttpSocketReader.class;
    }

}
