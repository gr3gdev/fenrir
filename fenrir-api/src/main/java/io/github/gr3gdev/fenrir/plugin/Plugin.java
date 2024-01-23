package io.github.gr3gdev.fenrir.plugin;

import io.github.gr3gdev.fenrir.validator.Validator;

import java.util.Properties;

/**
 * Fenrir plugin interface.
 * <p>
 * Use this interface for add a plugin. For example :
 * <ul>
 * <li>resolve a response of a request with a JSON mapping,</li>
 * <li>add JPA support</li>
 * </ul>
 */
public interface Plugin {
    /**
     * Init the plugin.
     *
     * @param mainClass        the main class
     * @param fenrirProperties fenrir properties
     */
    default void init(Class<?> mainClass, Properties fenrirProperties) {
        // None
    }

    /**
     * Add a validator to the plugin.
     *
     * @param validator a validator
     */
    void addValidator(Validator validator);
}
