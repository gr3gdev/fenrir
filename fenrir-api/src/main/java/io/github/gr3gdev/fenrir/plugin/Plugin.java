package io.github.gr3gdev.fenrir.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.annotation.Param;

public abstract class Plugin<M, RQ extends Request, RS extends Response> {

    @SuppressWarnings("unchecked")
    public final RS process(Object instance, Method method, RQ request, String contentType) {
        final Object[] parameterValues = Arrays.stream(method.getParameters())
                .map(p -> extractParameter(p, request))
                .toArray(Object[]::new);
        try {
            final Object methodReturn = method.invoke(instance, parameterValues);
            if (methodReturn.getClass().isAssignableFrom(getReturnMethodClass())) {
                return process((M) methodReturn, contentType);
            } else {
                throw new PluginException(
                        "The return type of the method in " + instance.getClass().getCanonicalName() + " must be "
                                + getReturnMethodClass().getCanonicalName());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Class<M> getReturnMethodClass();

    protected Object extractParameter(Parameter parameter, RQ request) {
        if (parameter.isAnnotationPresent(Param.class)) {
            final Param param = parameter.getAnnotation(Param.class);
            return request.param(param.value()).orElse(null);
        } else {
            return null;
        }
    }

    public abstract RS process(M methodReturn, String contentType);
}
