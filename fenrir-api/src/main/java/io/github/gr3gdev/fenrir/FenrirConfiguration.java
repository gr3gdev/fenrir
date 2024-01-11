package io.github.gr3gdev.fenrir;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.runtime.Mode;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FenrirConfiguration {
    int port() default 9000;

    Class<? extends Plugin<?, ? extends Request, ? extends Response>>[] plugins() default {};

    Class<? extends Mode<? extends SocketEvent, ? extends Request, ? extends Response>>[] modes();
}
