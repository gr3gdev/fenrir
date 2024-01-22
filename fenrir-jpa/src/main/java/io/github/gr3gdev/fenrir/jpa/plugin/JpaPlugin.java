package io.github.gr3gdev.fenrir.jpa.plugin;

import io.github.gr3gdev.fenrir.jpa.JPAManager;
import io.github.gr3gdev.fenrir.plugin.Plugin;

import java.util.Properties;

/**
 * Plugin for JPA.
 */
public class JpaPlugin implements Plugin {
    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Class<?> mainClass, Properties fenrirProperties) {
        JPAManager.init(mainClass, fenrirProperties);
    }
}
