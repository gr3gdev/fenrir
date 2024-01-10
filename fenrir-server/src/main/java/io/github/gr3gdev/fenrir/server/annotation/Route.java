package io.github.gr3gdev.fenrir.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.gr3gdev.fenrir.server.plugin.Plugin;
import io.github.gr3gdev.fenrir.server.plugin.impl.FileLoaderPlugin;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    Class<? extends Plugin<?>> plugin() default FileLoaderPlugin.class;
}
