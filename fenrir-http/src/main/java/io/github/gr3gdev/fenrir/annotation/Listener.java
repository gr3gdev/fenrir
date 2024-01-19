package io.github.gr3gdev.fenrir.annotation;

import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for execute a methode when the route match with the path.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
    /**
     * The response code HTTP, 200 by default.
     *
     * @return HttpStatus
     */
    HttpStatus responseCode() default HttpStatus.OK;

    /**
     * The HTTP method, GET by default.
     *
     * @return HttpMethod
     */
    HttpMethod method() default HttpMethod.GET;

    /**
     * The path to intercept.
     *
     * @return String
     */
    String path();

    /**
     * The content-type for the response, text/html by default.
     *
     * @return String
     */
    String contentType() default "text/html";
}
