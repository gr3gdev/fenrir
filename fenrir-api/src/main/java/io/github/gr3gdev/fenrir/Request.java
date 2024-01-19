package io.github.gr3gdev.fenrir;

import java.util.Optional;

/**
 * Interface for request receive by the server.
 */
public interface Request {

    /**
     * Get the request parameter value.
     *
     * @param key the key of the parameter
     * @return parameter value
     */
    Optional<String> param(String key);

}
