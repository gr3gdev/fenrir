package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.event.SocketEvent;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.runtime.Mode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FenrirConfiguration {
    int port() default 9000;

    Class<? extends Plugin>[] plugins() default {};

    Class<? extends Mode<? extends SocketEvent>>[] modes();
}
