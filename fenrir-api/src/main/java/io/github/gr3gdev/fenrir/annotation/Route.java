package io.github.gr3gdev.fenrir.annotation;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.plugin.SocketPlugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    String path() default "/";

    Class<? extends SocketPlugin<?, ? extends Request, ? extends Response>> plugin();
}
