package io.github.gr3gdev.sample;

import io.github.gr3gdev.common.thymeleaf.User;
import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.file.plugin.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.sample.route.StaticRoute;
import io.github.gr3gdev.sample.route.UserRoute;

@FenrirConfiguration(plugins = {ThymeleafPlugin.class, FileLoaderPlugin.class, JpaPlugin.class}, modes = {HttpMode.class})
@HttpConfiguration(routes = {UserRoute.class, StaticRoute.class})
@JpaConfiguration(entitiesClass = {User.class})
public class ThymeleafApp {
    public static void main(String[] args) {
        FenrirApplication.run(ThymeleafApp.class);
    }
}