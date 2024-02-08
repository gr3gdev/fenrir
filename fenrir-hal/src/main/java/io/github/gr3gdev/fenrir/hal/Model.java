package io.github.gr3gdev.fenrir.hal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
public abstract class Model {
    @JsonProperty("_links")
    private Map<String, Map<String, String>> links;
    @JsonProperty("_embedded")
    private List<Model> embedded;

    public void addLink(String name, Class<?> routeClass, String listenerRef, String id) {
        final String listenerPath = Arrays.stream(routeClass.getMethods()).parallel()
                .filter(m -> m.isAnnotationPresent(Listener.class) && compareRef(listenerRef, m))
                .findFirst()
                .map(m -> m.getAnnotation(Listener.class))
                .orElseThrow()
                .path();
        final String href = routeClass.getAnnotation(Route.class).path() + listenerPath + id;
        links.put(name, Map.of("href", href.replace("//", "/")));
    }

    private static boolean compareRef(String listenerRef, Method m) {
        String ref = m.getAnnotation(Listener.class).ref();
        if (ref.equals("{methodName}")) {
            ref = m.getName();
        }
        return ref.equals(listenerRef);
    }
}
