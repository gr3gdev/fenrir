package io.github.gr3gdev.fenrir.socket;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpRequest;
import io.github.gr3gdev.fenrir.http.HttpRouteListener;
import lombok.Getter;

public final class HttpSocketEvent implements SocketEvent {

    @Getter
    private final HttpMethod method;
    @Getter
    private final HttpRouteListener routeListener;
    private final Map<Integer, String> pathParameters = new HashMap<>();
    private String patternPath;

    public HttpSocketEvent(String path, HttpMethod method, HttpRouteListener routeListener) {
        this.method = method;
        this.routeListener = routeListener;
        this.patternPath = "^" + path.replace("/", "\\/") + "$";
        final Pattern pattern = Pattern.compile("\\{\\w*}");
        final Matcher matcher = pattern.matcher(path);
        var index = 1;
        while (matcher.find()) {
            final String paramName = path.substring(matcher.start() + 1, matcher.end() - 1);
            patternPath = patternPath.replace("{" + paramName + "}", "(.+)");
            pathParameters.put(index, paramName);
            index++;
        }
    }

    @Override
    public boolean match(Request request) {
        final HttpRequest httpRequest = (HttpRequest) request;
        final Matcher matcher = Pattern.compile(patternPath).matcher(httpRequest.path());
        var matching = false;
        if (matcher.find() && method.name().equalsIgnoreCase(httpRequest.method())) {
            matching = true;
            if (matcher.groupCount() > 0 && !pathParameters.isEmpty()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    httpRequest.params(pathParameters.get(i), matcher.group(i));
                }
            }
        }
        return matching;
    }
}
