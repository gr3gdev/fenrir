package io.github.gr3gdev.fenrir.thymeleaf.context.web;

import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafResponse;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.IWebSession;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * {@link IWebExchange} implementation for the Fenrir {@link io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin}.
 */
@RequiredArgsConstructor
public class FenrirWebExchange implements IWebExchange {
    private final WebRequestAdapter webRequestAdapter;
    private final ThymeleafResponse thymeleafResponse;

    @Override
    public IWebRequest getRequest() {
        return webRequestAdapter;
    }

    @Override
    public IWebSession getSession() {
        return null;
    }

    @Override
    public IWebApplication getApplication() {
        return null;
    }

    @Override
    public Principal getPrincipal() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return thymeleafResponse.locale();
    }

    @Override
    public String getContentType() {
        return webRequestAdapter.getHeaderValue("Content-Type");
    }

    @Override
    public String getCharacterEncoding() {
        return "UTF-8";
    }

    @Override
    public boolean containsAttribute(String name) {
        return getAttributeMap().containsKey(name);
    }

    @Override
    public int getAttributeCount() {
        return getAllAttributeNames().size();
    }

    @Override
    public Set<String> getAllAttributeNames() {
        return getAttributeMap().keySet();
    }

    @Override
    public Map<String, Object> getAttributeMap() {
        return thymeleafResponse.variables();
    }

    @Override
    public Object getAttributeValue(String name) {
        return getAttributeMap().get(name);
    }

    @Override
    public void setAttributeValue(String name, Object value) {
        thymeleafResponse.variables().put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        thymeleafResponse.variables().remove(name);
    }

    @Override
    public String transformURL(String url) {
        return url;
    }
}
