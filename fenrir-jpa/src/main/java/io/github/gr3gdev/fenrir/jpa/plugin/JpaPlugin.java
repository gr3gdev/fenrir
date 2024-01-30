package io.github.gr3gdev.fenrir.jpa.plugin;

import io.github.gr3gdev.fenrir.jpa.JPAManager;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.validator.PluginValidator;

import java.util.Properties;

/**
 * Plugin for JPA.
 */
public class JpaPlugin implements Plugin {
    @Override
    public void init(Class<?> mainClass, Properties fenrirProperties) {
        JPAManager.init(mainClass, fenrirProperties);
    }

    @Override
    public void addValidator(PluginValidator<?> validator) {
        // None
    }
}
