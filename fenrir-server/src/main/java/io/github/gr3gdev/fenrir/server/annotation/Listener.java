package io.github.gr3gdev.fenrir.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.gr3gdev.fenrir.server.http.RequestMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    RequestMethod method() default RequestMethod.GET;

    String path();

    String contentType() default "text/html";
}
