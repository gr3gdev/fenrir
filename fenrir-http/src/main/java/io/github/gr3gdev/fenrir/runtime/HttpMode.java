package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpRouteListener;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;
import io.github.gr3gdev.fenrir.socket.HttpSocketEvent;
import io.github.gr3gdev.fenrir.socket.HttpSocketReader;
import io.github.gr3gdev.fenrir.validator.RouteValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HTTP Mode (for example : application server, REST API).
 */
public class HttpMode implements Mode<HttpSocketEvent, HttpResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpMode.class);

    /**
     * Property : Content-Type.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * Property : Http-Code.
     */
    public static final String RESPONSE_CODE = "Http-Code";

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<HttpSocketEvent> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins, Properties fenrirProperties,
                                     List<Interceptor<?, HttpResponse, ?>> interceptors) {
        LOGGER.trace("Init HTTP runtime mode");
        final FileLoaderPlugin fileLoaderPlugin = new FileLoaderPlugin();
        plugins.put(FileLoaderPlugin.class, fileLoaderPlugin);
        final List<Class<?>> routes = parseRoutes(mainClass);
        final Set<HttpSocketEvent> socketEvents = new HashSet<>();
        final HttpResponse favicon = fileLoaderPlugin.process("/favicon.ico", Map.of(
                RESPONSE_CODE, HttpStatus.OK,
                CONTENT_TYPE, "image/vnd.microsoft.icon"
        ));
        final Map<Class<?>, RouteValidator> validatorCache = new HashMap<>();
        socketEvents.add(new HttpSocketEvent(
                "/favicon.ico", HttpMethod.GET, new HttpRouteListener((req) -> favicon)));
        socketEvents.addAll(routes.parallelStream()
                .map(routeClass -> findSocketEvents(plugins, routeClass, validatorCache, interceptors))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
        return socketEvents;
    }

    private List<Class<?>> parseRoutes(Class<?> mainClass) {
        final HttpConfiguration annotation = mainClass.getAnnotation(HttpConfiguration.class);
        if (annotation == null) {
            throw new RuntimeException("Missing @HttpConfiguration annotation on the main class : " + mainClass.getCanonicalName());
        }
        return Arrays.asList(annotation.routes());
    }

    private Set<HttpSocketEvent> findSocketEvents(Map<Class<?>, Plugin> plugins, Class<?> routeClass,
                                                  Map<Class<?>, RouteValidator> validatorCache,
                                                  List<Interceptor<?, HttpResponse, ?>> interceptors) {
        LOGGER.trace("Find socket events for {}", routeClass.getCanonicalName());
        final Route route = routeClass.getAnnotation(Route.class);
        initValidators(route.validators(), validatorCache);
        final HttpSocketPlugin<?> plugin = (HttpSocketPlugin<?>) plugins.get(route.plugin());
        if (plugin == null) {
            throw new RuntimeException("Missing plugin " + route.plugin().getCanonicalName() + " in @FenrirConfiguration");
        }
        return Arrays.stream(routeClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Listener.class))
                .map(m -> mapMethodToSocketEvent(route, routeClass, validatorCache, m, plugin, interceptors))
                .collect(Collectors.toSet());
    }

    private String constructPath(String parentPath, String path) {
        final String completePath = parentPath + path;
        return completePath.replace("//", "/");
    }

    private void initValidators(Class<? extends RouteValidator>[] validators, Map<Class<?>, RouteValidator> validatorCache) {
        Arrays.stream(validators)
                .parallel()
                .forEach(v -> validatorCache.computeIfAbsent(v, k -> {
                    LOGGER.trace("Init validator {}", v.getCanonicalName());
                    return (RouteValidator) ClassUtils.newInstance(v);
                }));
    }

    private HttpSocketEvent mapMethodToSocketEvent(Route route, Class<?> routeClass, Map<Class<?>, RouteValidator> validatorCache,
                                                   Method m, HttpSocketPlugin<?> plugin, List<Interceptor<?, HttpResponse, ?>> interceptors) {
        final Listener listenerAnnotation = m.getAnnotation(Listener.class);
        initValidators(listenerAnnotation.validators(), validatorCache);
        final List<RouteValidator> validators = validatorCache.entrySet().stream()
                .filter(entry -> Stream.concat(
                                Arrays.stream(route.validators()),
                                Arrays.stream(listenerAnnotation.validators())).toList()
                        .contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
        final Map<String, Object> properties = Map.of(
                CONTENT_TYPE, listenerAnnotation.contentType(),
                RESPONSE_CODE, listenerAnnotation.responseCode()
        );
        final String path = constructPath(route.path(), listenerAnnotation.path());
        LOGGER.trace("Create a socket event for {} {}", listenerAnnotation.method(), path);
        final HttpRouteListener routeListener = new HttpRouteListener(
                (req) -> plugin.process(routeClass, m, req, properties, validators, interceptors));
        return new HttpSocketEvent(path, listenerAnnotation.method(), routeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends SocketReader> getSocketReaderClass() {
        return HttpSocketReader.class;
    }

}
