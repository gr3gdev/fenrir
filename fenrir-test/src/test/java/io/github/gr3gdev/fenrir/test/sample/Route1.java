package io.github.gr3gdev.fenrir.test.sample;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;

@Route(plugin = JsonPlugin.class, path = "/test1")
public class Route1 {
    @Listener(path = "/")
    public String get() {
        return "1";
    }
}
