package io.github.gr3gdev.fenrir;

import java.util.Optional;

/**
 * Interface for requests.
 *
 * @author Gregory Tardivel
 */
public interface Request {

    /**
     * Get Request Parameter value.
     *
     * @return params
     */
    Optional<String> param(String key);

}
