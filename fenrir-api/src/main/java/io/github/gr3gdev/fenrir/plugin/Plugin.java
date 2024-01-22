package io.github.gr3gdev.fenrir.plugin;

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
}
