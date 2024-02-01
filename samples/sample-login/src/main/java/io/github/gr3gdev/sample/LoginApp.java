package io.github.gr3gdev.sample;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.security.SecurityConfiguration;
import io.github.gr3gdev.fenrir.security.runtime.SecurityMode;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.sample.bean.User;
import io.github.gr3gdev.sample.interceptor.UnauthorizedInterceptor;
import io.github.gr3gdev.sample.route.LoginRoute;
import io.github.gr3gdev.sample.service.UserService;
import io.github.gr3gdev.sample.validator.UsernamePasswordValidator;

@FenrirConfiguration(modes = {SecurityMode.class}, plugins = {ThymeleafPlugin.class, JpaPlugin.class},
        interceptors = {UnauthorizedInterceptor.class})
@HttpConfiguration(routes = {LoginRoute.class})
@JpaConfiguration(entitiesClass = {User.class})
@SecurityConfiguration(validator = UsernamePasswordValidator.class, service = UserService.class)
public class LoginApp {
    public static void main(String[] args) {
        FenrirApplication.run(LoginApp.class);
    }
}