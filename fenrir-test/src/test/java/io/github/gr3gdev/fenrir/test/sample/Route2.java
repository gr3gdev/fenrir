package io.github.gr3gdev.fenrir.test.sample;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;

@Route(plugin = JsonPlugin.class, path = "/test2")
public class Route2 {
    @Listener(path = "/")
    public String get() {
        return "2";
    }
}
