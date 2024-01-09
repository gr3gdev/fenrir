package io.github.gr3gdev.moon.server.socket;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.gr3gdev.moon.server.http.Request;
import io.github.gr3gdev.moon.server.http.RequestMethod;
import io.github.gr3gdev.moon.server.http.RouteListener;

public class SocketEvent {

    private final RequestMethod method;
    private final RouteListener routeListener;
    private final Map<Integer, String> pathParameters = new HashMap<>();
    private String patternPath;

    public SocketEvent(String path, RequestMethod method, RouteListener routeListener) {
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

    public boolean match(Request request) {
        final Matcher matcher = Pattern.compile(patternPath).matcher(request.path());
        var matching = false;
        if (matcher.find() && method.name().equalsIgnoreCase(request.method())) {
            matching = true;
            if (matcher.groupCount() > 0 && !pathParameters.isEmpty()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    request.params(pathParameters.get(i), matcher.group(i));
                }
            }
        }
        return matching;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public RouteListener getRouteListener() {
        return routeListener;
    }
}
