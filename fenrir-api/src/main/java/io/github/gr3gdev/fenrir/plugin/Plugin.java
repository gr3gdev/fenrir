package io.github.gr3gdev.fenrir.plugin;

public interface Plugin {
    default void init(Class<?> mainClass) {
        // None
    }
}
