package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpRouteListener;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.plugin.impl.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.plugin.impl.HttpSocketPlugin;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;
import io.github.gr3gdev.fenrir.reflect.PackageUtils;
import io.github.gr3gdev.fenrir.socket.HttpSocketEvent;
import io.github.gr3gdev.fenrir.socket.HttpSocketReader;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class HttpMode implements Mode<HttpSocketEvent> {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String RESPONSE_CODE = "Http-Code";

    @Override
    public Set<HttpSocketEvent> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins) {
        final FileLoaderPlugin fileLoaderPlugin = new FileLoaderPlugin();
        plugins.put(FileLoaderPlugin.class, fileLoaderPlugin);
        final List<Class<?>> routes = parseRoutes(mainClass);
        final Set<HttpSocketEvent> socketEvents = new HashSet<>();
        final HttpResponse favicon = fileLoaderPlugin.process("/favicon.ico", Map.of(
                RESPONSE_CODE, HttpStatus.OK,
                CONTENT_TYPE, "image/vnd.microsoft.icon"
        ));
        socketEvents.add(new HttpSocketEvent(
                "/favicon.ico", HttpMethod.GET, new HttpRouteListener((req) -> favicon)));
        socketEvents.addAll(routes.stream()
                .map(routeClass -> findSocketEvents(plugins, routeClass))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
        return socketEvents;
    }

    private List<Class<?>> parseRoutes(Class<?> mainClass) {
        return PackageUtils.findAnnotatedClasses(mainClass, Route.class);
    }

    private Set<HttpSocketEvent> findSocketEvents(final Map<Class<?>, Plugin> plugins, Class<?> routeClass) {
        final Route route = routeClass.getAnnotation(Route.class);
        final Object routeInstance = ClassUtils.newInstance(routeClass);
        final HttpSocketPlugin<?> plugin = (HttpSocketPlugin<?>) plugins.get(route.plugin());
        return Arrays.stream(routeClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Listener.class))
                .map(m -> mapMethodToSocketEvent(route.path(), routeInstance, m, plugin))
                .collect(Collectors.toSet());
    }

    private String constructPath(String parentPath, String path) {
        final String completePath = parentPath + path;
        return completePath.replace("//", "/");
    }

    private HttpSocketEvent mapMethodToSocketEvent(String parentPath, Object instance, Method m, HttpSocketPlugin<?> plugin) {
        final Listener listenerAnnotation = m.getAnnotation(Listener.class);
        final Map<String, Object> properties = Map.of(
                CONTENT_TYPE, listenerAnnotation.contentType(),
                RESPONSE_CODE, listenerAnnotation.responseCode()
        );
        final HttpRouteListener routeListener = new HttpRouteListener(
                (req) -> plugin.process(instance, m, req, properties));
        return new HttpSocketEvent(constructPath(parentPath, listenerAnnotation.path()), listenerAnnotation.method(), routeListener);
    }

    @Override
    public Class<? extends SocketReader> getSocketReaderClass() {
        return HttpSocketReader.class;
    }

}
