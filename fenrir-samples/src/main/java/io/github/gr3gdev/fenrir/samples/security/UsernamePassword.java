package io.github.gr3gdev.fenrir.samples.security;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.samples.jpa.JpaRest;
import io.github.gr3gdev.fenrir.samples.security.bean.User;
import io.github.gr3gdev.fenrir.samples.security.interceptor.UnauthorizedInterceptor;
import io.github.gr3gdev.fenrir.samples.security.route.LoginRoute;
import io.github.gr3gdev.fenrir.samples.security.service.UserService;
import io.github.gr3gdev.fenrir.samples.security.validator.UsernamePasswordValidator;
import io.github.gr3gdev.fenrir.security.SecurityConfiguration;
import io.github.gr3gdev.fenrir.security.runtime.SecurityMode;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;

import java.io.IOException;
import java.util.logging.LogManager;

@FenrirConfiguration(modes = {SecurityMode.class}, plugins = {ThymeleafPlugin.class, JpaPlugin.class},
        interceptors = {UnauthorizedInterceptor.class})
@HttpConfiguration(routes = {LoginRoute.class})
@JpaConfiguration(entitiesClass = {User.class})
@SecurityConfiguration(validator = UsernamePasswordValidator.class, service = UserService.class)
public class UsernamePassword {
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(JpaRest.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read logging.properties", e);
        }
        FenrirApplication.run(UsernamePassword.class);
    }
}
