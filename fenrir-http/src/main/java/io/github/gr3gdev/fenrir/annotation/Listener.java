package io.github.gr3gdev.fenrir.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.gr3gdev.fenrir.http.HttpMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    HttpMethod method() default HttpMethod.GET;

    String path();

    String contentType() default "text/html";
}
