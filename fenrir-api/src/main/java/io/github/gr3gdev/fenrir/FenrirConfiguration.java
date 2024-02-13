package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.runtime.Mode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for the Fenrir application.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FenrirConfiguration {
    /**
     * The list of the plugins.
     *
     * @return Array of Class
     */
    Class<? extends Plugin>[] plugins() default {};

    /**
     * The modes used for resolve {@link RouteListener}.
     *
     * @return Class
     */
    Class<? extends Mode<? extends RouteListener, ? extends ErrorListener, ? extends Request, ? extends Response>>[] modes();

    /**
     * List of classes with a {@link FenrirConfiguration} annotation. For example a configuration file in another module.
     *
     * @return Array of Class
     */
    Class<?>[] imports() default {};

    /**
     * List of interceptors.
     *
     * @return Array of Class
     */
    Class<? extends Interceptor<?, ? extends Response, ? extends Plugin>>[] interceptors() default {};
}
