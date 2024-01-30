package io.github.gr3gdev.fenrir.samples.security.route;

import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.samples.security.data.UserDetails;
import io.github.gr3gdev.fenrir.security.Secure;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafResponse;

import java.util.Locale;
import java.util.Map;

@Route(plugin = ThymeleafPlugin.class)
public class LoginRoute {

    @Listener(path = "/")
    public ThymeleafResponse login() {
        return new ThymeleafResponse("login.html", Map.of(), Locale.UK);
    }

    @Listener(path = "/", method = HttpMethod.POST)
    @Secure(roles = {"ADMIN", "USER"})
    public ThymeleafResponse authenticated(@Body UserDetails userDetails) {
        return new ThymeleafResponse("success.html", Map.of("username", userDetails.getUsername()), Locale.UK);
    }
}
