package io.github.gr3gdev.fenrir.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for automatically mapping HTTP requests parameter.
 * <p>
 * Example with a path variable :
 * <pre>{@code
 * @Listener(path = "/{my_param}")
 * public List<Person> find(@Param("my_param") String myParam) {
 *     // code
 * }
 * }</pre>
 * <p>
 * Example with a multipart/form-data :
 * <pre>{@code
 * @Listener(path = "/", method = HttpMethod.PORT)
 * public List<Person> add(@Param("firstname") String firstname, @Param("lastname") String lastname) {
 *     // code
 * }
 * }</pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    /**
     * The parameter value in the request : path variable or multipart/form-data.
     *
     * @return String
     */
    String value();
}
