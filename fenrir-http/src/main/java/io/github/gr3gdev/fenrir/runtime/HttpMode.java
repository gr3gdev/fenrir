package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpRouteListener;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
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
import java.util.function.Function;
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
        final Map<Class<?>, Object> routes = parseAndInitRoutes(mainClass);
        final Map<Class<?>, RouteValidator> validatorCache = new HashMap<>();
        return routes.entrySet().parallelStream()
                .map(entry -> findSocketEvents(plugins, entry.getKey(), entry.getValue(), validatorCache, interceptors))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    private Map<Class<?>, Object> parseAndInitRoutes(Class<?> mainClass) {
        final HttpConfiguration annotation = mainClass.getAnnotation(HttpConfiguration.class);
        if (annotation == null) {
            throw new RuntimeException("Missing @HttpConfiguration annotation on the main class : " + mainClass.getCanonicalName());
        }
        return Arrays.stream(annotation.routes())
                .parallel()
                .collect(Collectors.toMap(Function.identity(), ClassUtils::newInstance));
    }

    private Set<HttpSocketEvent> findSocketEvents(Map<Class<?>, Plugin> plugins,
                                                  Class<?> routeClass, Object routeInstance,
                                                  Map<Class<?>, RouteValidator> validatorCache,
                                                  List<Interceptor<?, HttpResponse, ?>> interceptors) {
        LOGGER.trace("Find socket events for {}", routeClass.getCanonicalName());
        final Route route = routeClass.getAnnotation(Route.class);
        initValidators(route.validators(), validatorCache);
        final HttpSocketPlugin<?> plugin = (HttpSocketPlugin<?>) plugins.get(route.plugin());
        if (plugin == null) {
            throw new RuntimeException("Missing plugin " + route.plugin().getCanonicalName() + " in @FenrirConfiguration");
        }
        final List<String> listenerRefs = new ArrayList<>();
        return Arrays.stream(routeClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Listener.class))
                .map(m -> mapMethodToSocketEvent(route, routeClass, routeInstance, validatorCache, m, plugin, interceptors, listenerRefs))
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

    private String listenerRef(Listener annotation, Method m) {
        if (annotation.ref().equals("{methodName}")) {
            return m.getName();
        } else {
            return annotation.ref();
        }
    }

    private HttpSocketEvent mapMethodToSocketEvent(Route route, Class<?> routeClass, Object routeInstance, Map<Class<?>, RouteValidator> validatorCache,
                                                   Method m, HttpSocketPlugin<?> plugin, List<Interceptor<?, HttpResponse, ?>> interceptors,
                                                   List<String> listenerRefs) {
        final Listener listenerAnnotation = m.getAnnotation(Listener.class);
        final String ref = listenerRef(listenerAnnotation, m);
        if (listenerRefs.contains(ref)) {
            throw new RuntimeException("Listener ref must be unique in the same Route : " + routeClass.getCanonicalName() + "#" + m.getName());
        }
        listenerRefs.add(ref);
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
                (req) -> plugin.process(routeClass, routeInstance, m, req, properties, validators, interceptors));
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
