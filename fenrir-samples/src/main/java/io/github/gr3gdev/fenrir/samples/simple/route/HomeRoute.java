package io.github.gr3gdev.fenrir.samples.simple.route;

import io.github.gr3gdev.fenrir.server.annotation.Listener;
import io.github.gr3gdev.fenrir.server.annotation.Route;

@Route
public class HomeRoute {

    @Listener(path = "/")
    public String home() {
        return "/simple/index.html";
    }
}
