package io.github.gr3gdev.fenrir.samples.thymeleaf.route;

import io.github.gr3gdev.fenrir.server.annotation.Listener;
import io.github.gr3gdev.fenrir.server.annotation.Param;
import io.github.gr3gdev.fenrir.server.annotation.Route;

@Route
public class StaticRoute {

    @Listener(path = "/css/{file}", contentType = "text/css")
    public String css(@Param("file") String cssFile) {
        return "/css/" + cssFile;
    }
}
