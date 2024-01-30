package io.github.gr3gdev.fenrir.samples.security.validator;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.samples.security.service.UserService;
import io.github.gr3gdev.fenrir.security.Secure;
import io.github.gr3gdev.fenrir.security.validator.SecurityValidator;
import io.github.gr3gdev.fenrir.validator.ValidatorException;

import java.util.Map;

public class UsernamePasswordValidator implements SecurityValidator<UserService> {
    private UserService userService;

    @Override
    public void validate(Request request, Map<String, Object> properties, Secure annotation) throws ValidatorException {
        final String username = request.param("username").orElse("");
        final String password = request.param("password").orElse("");
        if (!userService.isAuthenticated(username, password)) {
            throw new ValidatorException(HttpResponse.of(HttpStatus.UNAUTHORIZED));
        }
    }

    @Override
    public void setSecurityService(UserService service) {
        this.userService = service;
    }
}
