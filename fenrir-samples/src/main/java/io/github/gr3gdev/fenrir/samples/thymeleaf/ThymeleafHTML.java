package io.github.gr3gdev.fenrir.samples.thymeleaf;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;

@FenrirConfiguration(plugins = { ThymeleafPlugin.class })
public class ThymeleafHTML {

    public static void main(String[] args) {
        FenrirApplication.run(ThymeleafHTML.class);
    }
}
