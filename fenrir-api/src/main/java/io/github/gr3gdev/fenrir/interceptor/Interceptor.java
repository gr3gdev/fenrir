package io.github.gr3gdev.fenrir.interceptor;

import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.plugin.Plugin;

import java.util.function.Supplier;

/**
 * Interface of interceptor.
 *
 * @param <T> Type return by the route method
 * @param <R> Type return by the object to intercept
 * @param <P> Type of the plugin used to resolve the route method
 */
public interface Interceptor<T, R, P extends Plugin> {
    /**
     * Check if the interceptor supports this object.
     *
     * @param object the object
     * @return boolean
     */
    boolean supports(Object object);

    /**
     * Return the plugin class.
     *
     * @return Class of the plugin
     */
    Class<P> getPluginClass();

    /**
     * Replace the return of an original method.
     *
     * @param originalMethod the original method
     * @return New value of {@link Response}
     */
    T replace(Supplier<R> originalMethod);
}
