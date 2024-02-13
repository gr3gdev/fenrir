package io.github.gr3gdev.fenrir;

import java.util.concurrent.ConcurrentMap;

public record Listeners<RE extends Request, R extends RouteListener, E extends ErrorListener>
        (ConcurrentMap<RE, R> listeners, E errorListener) {
}
