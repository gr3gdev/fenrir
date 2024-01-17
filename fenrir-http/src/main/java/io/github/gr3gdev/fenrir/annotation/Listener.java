package io.github.gr3gdev.fenrir.annotation;

import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    HttpStatus responseCode() default HttpStatus.OK;

    HttpMethod method() default HttpMethod.GET;

    String path();

    String contentType() default "text/html";
}
