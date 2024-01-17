package io.github.gr3gdev.fenrir.jpa.plugin;

import io.github.gr3gdev.fenrir.jpa.JPAManager;
import io.github.gr3gdev.fenrir.plugin.Plugin;

public class JpaPlugin implements Plugin {
    @Override
    public void init(Class<?> mainClass) {
        JPAManager.init(mainClass);
    }
}
