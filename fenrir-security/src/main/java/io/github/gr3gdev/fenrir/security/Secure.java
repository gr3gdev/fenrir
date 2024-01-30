package io.github.gr3gdev.fenrir.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for secure a route.
 * <p>
 * Example :
 * <pre>{@code
 * @Secure
 * public String login(@Param("username") String username, @Param("password") String password) {
 *     // code
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure {
    /**
     * List of roles authorized.
     *
     * @return Array of String
     */
    String[] roles() default {};
}
