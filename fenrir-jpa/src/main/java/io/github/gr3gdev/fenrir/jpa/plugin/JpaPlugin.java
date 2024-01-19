package io.github.gr3gdev.fenrir.jpa.plugin;

import io.github.gr3gdev.fenrir.jpa.JPAManager;
import io.github.gr3gdev.fenrir.plugin.Plugin;

/**
 * Plugin for JPA.
 */
public class JpaPlugin implements Plugin {
    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Class<?> mainClass) {
        JPAManager.init(mainClass);
    }
}
