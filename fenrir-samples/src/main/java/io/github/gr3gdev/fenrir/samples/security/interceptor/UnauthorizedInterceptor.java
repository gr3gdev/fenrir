package io.github.gr3gdev.fenrir.samples.security.interceptor;

import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafResponse;
import io.github.gr3gdev.fenrir.validator.ValidatorException;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class UnauthorizedInterceptor implements Interceptor<ThymeleafResponse, Response, ThymeleafPlugin> {
    @Override
    public boolean supports(Object object) {
        return object instanceof ValidatorException exception
                && exception.getResponse() instanceof HttpResponse res
                && res.getStatus().equals(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public Class<ThymeleafPlugin> getPluginClass() {
        return ThymeleafPlugin.class;
    }

    @Override
    public ThymeleafResponse replace(Supplier<Response> originalMethod) {
        return new ThymeleafResponse("login.html", Map.of("error", "Username or password are invalid"), Locale.UK);
    }

}
