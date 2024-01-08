package com.github.gr3gdev.moon.server.http;

import java.util.Set;

import com.github.gr3gdev.moon.server.plugin.ServerPlugin;

/**
 * Response.
 *
 * @author Gregory Tardivel
 */
public class Route {

    private Request request;
    private Set<ServerPlugin> plugins;

    public Route(Request request, Set<ServerPlugin> plugins) {
        this.request = request;
        this.plugins = plugins;
    }

    /**
     * Get plugin.
     *
     * @param pClass Class of plugin
     * @return ServerPlugin
     */
    @SuppressWarnings("unchecked")
    public <P extends ServerPlugin> P plugin(Class<P> pClass) {
        if (plugins == null) {
            throw new RuntimeException("No plugins defined");
        }
        return plugins.stream()
                .filter(it -> it.getClass() == pClass)
                .map(it -> (P) it)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Plugin undefined " + pClass.getSimpleName()));
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Set<ServerPlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(Set<ServerPlugin> plugins) {
        this.plugins = plugins;
    }
}
