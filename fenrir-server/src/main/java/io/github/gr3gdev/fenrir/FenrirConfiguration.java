package io.github.gr3gdev.fenrir;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.gr3gdev.fenrir.server.plugin.Plugin;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FenrirConfiguration {
    int port() default 9000;

    Class<? extends Plugin<?>>[] plugins() default {};
}
