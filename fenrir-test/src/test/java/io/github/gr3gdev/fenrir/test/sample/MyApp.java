package io.github.gr3gdev.fenrir.test.sample;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;

@FenrirConfiguration(modes = {HttpMode.class}, plugins = {JsonPlugin.class})
@HttpConfiguration(routes = {TestRoute.class, Route1.class, Route2.class, Route3.class, Route4.class, Route5.class,
        Route6.class, Route7.class, Route8.class, Route9.class, Route10.class})
public class MyApp {
    public static void main(String[] args) {
        FenrirApplication.run(MyApp.class);
    }
}
