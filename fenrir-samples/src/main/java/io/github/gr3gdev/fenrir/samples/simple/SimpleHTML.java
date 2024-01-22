package io.github.gr3gdev.fenrir.samples.simple;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.fenrir.samples.simple.route.HomeRoute;

@FenrirConfiguration(modes = {HttpMode.class})
@HttpConfiguration(routes = HomeRoute.class)
public class SimpleHTML {

    public static void main(String[] args) {
        FenrirApplication.run(SimpleHTML.class);
    }
}
