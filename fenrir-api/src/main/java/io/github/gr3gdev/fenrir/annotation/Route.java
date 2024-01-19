package io.github.gr3gdev.fenrir.annotation;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.plugin.SocketPlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for automatically mapping HTTP requests.
 * <p>
 * Example :
 * <pre>{@code
 * @Route(plugin = DefaultPlugin.class, path = "/context")
 * public class ContextRoute {
 *     // code
 * }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    /**
     * The principal path of the route, / by default.
     *
     * @return String
     */
    String path() default "/";

    /**
     * The plugin used for resolve the {@link Response}.
     *
     * @return Class
     */
    Class<? extends SocketPlugin<?, ? extends Request, ? extends Response>> plugin();
}
