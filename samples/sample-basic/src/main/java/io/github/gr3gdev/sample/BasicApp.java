package io.github.gr3gdev.sample;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.sample.route.HomeRoute;

@FenrirConfiguration(modes = {HttpMode.class})
@HttpConfiguration(routes = HomeRoute.class)
public class BasicApp {

    public static void main(String[] args) {
        FenrirApplication.run(BasicApp.class);
    }
}