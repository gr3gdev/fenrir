package io.github.gr3gdev.fenrir.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for automatically mapping HTTP requests body.
 * <p>
 * Example :
 * <pre>{@code
 * @Listener(path = "/", method = HttpMethod.POST)
 * public List<Person> add(@Body Person person) {
 *     // code
 * }
 * }</pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Body {
}
