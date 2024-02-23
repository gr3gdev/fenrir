package io.github.gr3gdev.fenrir.thymeleaf.context;

import io.github.gr3gdev.fenrir.http.HttpRequest;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.web.IWebRequest;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter for {@link HttpRequest} to {@link IWebRequest}.
 */
@RequiredArgsConstructor
public class WebRequestAdapter implements IWebRequest {
    private final HttpRequest request;

    @Override
    public String getMethod() {
        return request.method();
    }

    @Override
    public String getScheme() {
        return request.protocol();
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public Integer getServerPort() {
        return null;
    }

    @Override
    public String getApplicationPath() {
        return "/";
    }

    @Override
    public String getPathWithinApplication() {
        return request.path();
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public boolean containsHeader(String name) {
        return request.header(name).isPresent();
    }

    @Override
    public int getHeaderCount() {
        return request.headersNames().size();
    }

    @Override
    public Set<String> getAllHeaderNames() {
        return request.headersNames();
    }

    @Override
    public Map<String, String[]> getHeaderMap() {
        return request.headers().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().split(";")));
    }

    @Override
    public String[] getHeaderValues(String name) {
        return request.header(name)
                .map(v -> v.split(";"))
                .orElse(new String[0]);
    }

    @Override
    public boolean containsParameter(String name) {
        return request.param(name).isPresent();
    }

    @Override
    public int getParameterCount() {
        return request.paramsNames().size();
    }

    @Override
    public Set<String> getAllParameterNames() {
        return request.paramsNames();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return request.params().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().split(";")));
    }

    @Override
    public String[] getParameterValues(String name) {
        return request.param(name)
                .map(v -> v.split(";"))
                .orElse(new String[0]);
    }

    @Override
    public boolean containsCookie(String name) {
        return false;
    }

    @Override
    public int getCookieCount() {
        return 0;
    }

    @Override
    public Set<String> getAllCookieNames() {
        return null;
    }

    @Override
    public Map<String, String[]> getCookieMap() {
        return null;
    }

    @Override
    public String[] getCookieValues(String name) {
        return new String[0];
    }
}
