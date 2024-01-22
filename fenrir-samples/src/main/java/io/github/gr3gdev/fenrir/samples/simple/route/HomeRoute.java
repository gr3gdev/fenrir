package io.github.gr3gdev.fenrir.samples.simple.route;

import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.plugin.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.annotation.Listener;

@Route(plugin = FileLoaderPlugin.class)
public class HomeRoute {

    @Listener(path = "/")
    public String home() {
        return "/simple/index.html";
    }
}
