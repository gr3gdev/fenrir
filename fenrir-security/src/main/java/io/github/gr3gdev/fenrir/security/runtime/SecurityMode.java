package io.github.gr3gdev.fenrir.security.runtime;

import io.github.gr3gdev.fenrir.http.HttpResponse;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.plugin.Plugin;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.fenrir.security.SecurityConfiguration;
import io.github.gr3gdev.fenrir.security.service.SecurityService;
import io.github.gr3gdev.fenrir.security.validator.SecurityValidator;
import io.github.gr3gdev.fenrir.socket.HttpSocketEvent;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * HTTP Mode with security (for example : application server, REST API).
 */
public class SecurityMode extends HttpMode {
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Set<HttpSocketEvent> init(Class<?> mainClass, Map<Class<?>, Plugin> plugins, Properties fenrirProperties,
                                     List<Interceptor<?, HttpResponse, ?>> interceptors) {
        final SecurityConfiguration securityConfiguration = mainClass.getAnnotation(SecurityConfiguration.class);
        if (securityConfiguration == null) {
            throw new RuntimeException("Missing @SecurityConfiguration annotation on the main class : " + mainClass.getCanonicalName());
        }
        // Add the validator to all plugins
        final SecurityValidator validator = (SecurityValidator) ClassUtils.newInstance(securityConfiguration.validator());
        final SecurityService service = (SecurityService) ClassUtils.newInstance(securityConfiguration.service());
        validator.setSecurityService(service);
        plugins.values().forEach(plugin -> plugin.addValidator(validator));
        return super.init(mainClass, plugins, fenrirProperties, interceptors);
    }
}
