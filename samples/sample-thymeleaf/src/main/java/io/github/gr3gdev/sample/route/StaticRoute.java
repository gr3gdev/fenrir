package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.plugin.FileLoaderPlugin;

@Route(plugin = FileLoaderPlugin.class)
public class StaticRoute {

    @Listener(path = "/css/{file}", contentType = "text/css")
    public String css(@Param("file") String cssFile) {
        return "/css/" + cssFile;
    }
}
