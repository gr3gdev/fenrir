package io.github.gr3gdev.fenrir.samples.jpa;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.plugin.impl.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpMode;

@FenrirConfiguration(modes = {HttpMode.class}, plugins = {JsonPlugin.class, JpaPlugin.class})
public class JpaRest {
    public static void main(String[] args) {
        FenrirApplication.run(JpaRest.class);
    }
}
