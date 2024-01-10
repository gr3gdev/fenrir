package io.github.gr3gdev.fenrir.samples.thymeleaf.route;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.github.gr3gdev.fenrir.server.annotation.Listener;
import io.github.gr3gdev.fenrir.server.annotation.Route;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafResponse;

@Route(plugin = ThymeleafPlugin.class)
public class HomeRoute {

    @Listener(path = "/")
    public ThymeleafResponse home() {
        final Map<String, Object> variables = new HashMap<>();
        variables.put("welcome", "Welcome on thymeleaf page !");
        return new ThymeleafResponse("index.html", variables, Locale.ENGLISH);
    }
}
