package io.github.gr3gdev.fenrir.runtime;

import io.github.gr3gdev.fenrir.SocketReader;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpRouteListener;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.plugin.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.plugin.HttpSocketPlugin;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;
import io.github.gr3gdev.fenrir.socket.HttpSocketEvent;
import io.github.gr3gdev.fenrir.socket.HttpSocketReader;
import io.github.gr3gdev.fenrir.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HTTP Mode (for example : application server, REST API).
 */
public class HttpMode implements Mode<HttpSocketEvent> {

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
    public Set<HttpSocketEvent> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins, Properties fenrirProperties) {
        LOGGER.trace("Init HTTP runtime mode");
        final FileLoaderPlugin fileLoaderPlugin = new FileLoaderPlugin();
        plugins.put(FileLoaderPlugin.class, fileLoaderPlugin);
        final List<Class<?>> routes = parseRoutes(mainClass);
        final Set<HttpSocketEvent> socketEvents = new HashSet<>();
        final HttpResponse favicon = fileLoaderPlugin.process("/favicon.ico", Map.of(
                RESPONSE_CODE, HttpStatus.OK,
                CONTENT_TYPE, "image/vnd.microsoft.icon"
        ));
        final Map<Class<?>, Validator> validatorCache = new HashMap<>();
        socketEvents.add(new HttpSocketEvent(
                "/favicon.ico", HttpMethod.GET, new HttpRouteListener((req) -> favicon)));
        socketEvents.addAll(routes.parallelStream()
                .map(routeClass -> findSocketEvents(plugins, routeClass, validatorCache))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
        return socketEvents;
    }

    private List<Class<?>> parseRoutes(Class<?> mainClass) {
        return Arrays.asList(mainClass.getAnnotation(HttpConfiguration.class).routes());
    }

    private Set<HttpSocketEvent> findSocketEvents(Map<Class<?>, Plugin> plugins, Class<?> routeClass, Map<Class<?>, Validator> validatorCache) {
        LOGGER.trace("Find socket events for {}", routeClass.getCanonicalName());
        final Route route = routeClass.getAnnotation(Route.class);
        initValidators(route.validators(), validatorCache);
        final HttpSocketPlugin<?> plugin = (HttpSocketPlugin<?>) plugins.get(route.plugin());
        return Arrays.stream(routeClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Listener.class))
                .map(m -> mapMethodToSocketEvent(route, routeClass, validatorCache, m, plugin))
                .collect(Collectors.toSet());
    }

    private String constructPath(String parentPath, String path) {
        final String completePath = parentPath + path;
        return completePath.replace("//", "/");
    }

    private void initValidators(Class<? extends Validator>[] validators, Map<Class<?>, Validator> validatorCache) {
        Arrays.stream(validators)
                .parallel()
                .forEach(v -> validatorCache.computeIfAbsent(v, k -> {
                    LOGGER.trace("Init validator {}", v.getCanonicalName());
                    return (Validator) ClassUtils.newInstance(v);
                }));
    }

    private HttpSocketEvent mapMethodToSocketEvent(Route route, Class<?> routeClass, Map<Class<?>, Validator> validatorCache,
                                                   Method m, HttpSocketPlugin<?> plugin) {
        final Listener listenerAnnotation = m.getAnnotation(Listener.class);
        initValidators(listenerAnnotation.validators(), validatorCache);
        final List<Validator> validators = validatorCache.entrySet().stream()
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
                (req) -> plugin.process(routeClass, m, req, properties, validators));
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
