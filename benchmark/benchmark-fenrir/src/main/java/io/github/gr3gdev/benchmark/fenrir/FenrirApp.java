package io.github.gr3gdev.benchmark.fenrir;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.plugin.impl.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpMode;

@FenrirConfiguration(
        port = 9003,
        plugins = {JsonPlugin.class, JpaPlugin.class},
        modes = {HttpMode.class}
)
public class FenrirApp {
    public static void main(String[] args) {
        FenrirApplication.run(FenrirApp.class);
    }
}
