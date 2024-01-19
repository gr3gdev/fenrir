package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
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
     * The port, 9000 by default.
     *
     * @return int
     */
    int port() default 9000;

    /**
     * The list of the plugins.
     *
     * @return Array of Class
     */
    Class<? extends Plugin>[] plugins() default {};

    /**
     * The modes used for resolve {@link SocketEvent}.
     *
     * @return Class
     */
    Class<? extends Mode<? extends SocketEvent>>[] modes();
}
