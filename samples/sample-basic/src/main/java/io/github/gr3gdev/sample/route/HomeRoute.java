package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.plugin.FileLoaderPlugin;

@Route(plugin = FileLoaderPlugin.class)
public class HomeRoute {

    @Listener(path = "/")
    public String home() {
        return "/pages/index.html";
    }
}