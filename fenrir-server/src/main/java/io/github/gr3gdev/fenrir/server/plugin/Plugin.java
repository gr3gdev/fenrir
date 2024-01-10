package io.github.gr3gdev.fenrir.server.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import io.github.gr3gdev.fenrir.server.annotation.Param;
import io.github.gr3gdev.fenrir.server.http.Request;
import io.github.gr3gdev.fenrir.server.http.Response;

public abstract class Plugin<R> {

    @SuppressWarnings("unchecked")
    public final Response process(Object instance, Method method, Request request, String contentType) {
        final Object[] parameterValues = Arrays.stream(method.getParameters())
                .map(p -> extractParameter(p, request))
                .toArray(Object[]::new);
        try {
            final Object methodReturn = method.invoke(instance, parameterValues);
            if (methodReturn.getClass().isAssignableFrom(getReturnMethodClass())) {
                return process((R) methodReturn, contentType);
            } else {
                throw new PluginException(
                        "The return type of the method in " + instance.getClass().getCanonicalName() + " must be "
                                + getReturnMethodClass().getCanonicalName());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Class<R> getReturnMethodClass();

    protected Object extractParameter(Parameter parameter, Request request) {
        if (parameter.isAnnotationPresent(Param.class)) {
            final Param param = parameter.getAnnotation(Param.class);
            return request.params(param.value()).orElse(null);
        } else {
            return null;
        }
    }

    public abstract Response process(R methodReturn, String contentType);
}
