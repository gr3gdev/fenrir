package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.file.plugin.FileLoaderPlugin;
import io.github.gr3gdev.fenrir.http.ConditionalRequest;
import io.github.gr3gdev.fenrir.http.HttpRequest;
import lombok.NoArgsConstructor;

@Route(plugin = FileLoaderPlugin.class)
public class StaticRoute {
    @Listener(path = "/css/{file}", contentType = "text/css")
    public String css(@Param("file") String cssFile) {
        return "/static/css/" + cssFile;
    }

    @Listener(path = "/js/{file}", contentType = "application/javascript")
    public String javascript(@Param("file") String jsFile) {
        return "/static/js/" + jsFile;
    }

    @Listener(path = "/webfonts/{file}", conditionalContentType = FontConditionalRequest.class)
    public String webfontsWoff(@Param("file") String file) {
        return "/static/webfonts/" + file;
    }

    @NoArgsConstructor
    public static class FontConditionalRequest implements ConditionalRequest {

        @Override
        public String findContentType(HttpRequest httpRequest) {
            final String file = httpRequest.param("file").orElseThrow();
            if (file.endsWith(".woff")) {
                return "font/woff";
            } else if (file.endsWith(".woff2")) {
                return "font/woff2";
            } else if (file.endsWith(".ttf")) {
                return "font/ttf";
            }
            return null;
        }
    }
}
