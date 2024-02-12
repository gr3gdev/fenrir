package io.github.gr3gdev.fenrir.test.sample;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;

@FenrirConfiguration(modes = {HttpMode.class}, plugins = {JsonPlugin.class})
@HttpConfiguration(routes = {CustomRoute.class})
public class MyApp {
    public static void main(String[] args) {
        FenrirApplication.run(MyApp.class);
    }
}
