package io.github.gr3gdev.fenrir.plugin;

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
    default void init(Class<?> mainClass) {
        // None
    }
}
