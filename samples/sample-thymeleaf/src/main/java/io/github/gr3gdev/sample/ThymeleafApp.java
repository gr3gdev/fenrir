package io.github.gr3gdev.sample;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.file.plugin.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.sample.route.HomeRoute;
import io.github.gr3gdev.sample.route.StaticRoute;

@FenrirConfiguration(plugins = {ThymeleafPlugin.class, FileLoaderPlugin.class}, modes = {HttpMode.class})
@HttpConfiguration(routes = {HomeRoute.class, StaticRoute.class})
public class ThymeleafApp {

    public static void main(String[] args) {
        FenrirApplication.run(ThymeleafApp.class);
    }
}