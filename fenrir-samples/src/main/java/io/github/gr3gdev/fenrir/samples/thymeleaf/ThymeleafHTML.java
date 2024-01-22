package io.github.gr3gdev.fenrir.samples.thymeleaf;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.fenrir.samples.thymeleaf.route.HomeRoute;
import io.github.gr3gdev.fenrir.samples.thymeleaf.route.StaticRoute;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;

@FenrirConfiguration(plugins = {ThymeleafPlugin.class}, modes = {HttpMode.class})
@HttpConfiguration(routes = {HomeRoute.class, StaticRoute.class})
public class ThymeleafHTML {

    public static void main(String[] args) {
        FenrirApplication.run(ThymeleafHTML.class);
    }
}
