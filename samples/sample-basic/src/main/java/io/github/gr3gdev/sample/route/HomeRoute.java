package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.file.plugin.FileLoaderPlugin;

@Route(plugin = FileLoaderPlugin.class)
public class HomeRoute {

    @Listener(path = "/favicon.ico", contentType = "image/vnd.microsoft.icon")
    public String favicon() {
        return "/favicon.ico";
    }

    @Listener(path = "/")
    public String home() {
        return "/pages/index.html";
    }
}