package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Route(plugin = ThymeleafPlugin.class)
public class HomeRoute {

    @Listener(path = "/")
    public ThymeleafResponse home() {
        return update(Locale.UK.getLanguage());
    }

    @Listener(path = "/", method = HttpMethod.POST)
    public ThymeleafResponse update(@Param("locale") String lang) {
        final Locale currentLocale = Locale.of(lang);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("welcome", "Welcome on thymeleaf page !");
        variables.put("locale", currentLocale);
        variables.put("locales", List.of(Locale.UK, Locale.FRANCE, Locale.GERMANY));
        return new ThymeleafResponse("index.html", variables, currentLocale);
    }
}
