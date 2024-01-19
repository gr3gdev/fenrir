package io.github.gr3gdev.fenrir.plugin;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;

public abstract class SocketPlugin<M, RQ extends Request, RS extends Response> implements Plugin {

    @SuppressWarnings("unchecked")
    public final RS process(Class<?> routeClass, Method method, RQ request, Map<String, Object> properties) {
        final Map<String, Class<?>> genericClasses = ClassUtils.findGenericClasses(routeClass);
        final Object routeInstance = ClassUtils.newInstance(routeClass);
        final Map<String, Class<?>> parameterClasses = ClassUtils.findGenericClasses(method, genericClasses);
        final Object[] parameterValues = Arrays.stream(method.getParameters())
                .map(p -> extractParameter(p, request, parameterClasses.get(p.getName())))
                .toArray(Object[]::new);
        try {
            final Object methodReturn = method.invoke(routeInstance, parameterValues);
            return process((M) methodReturn, properties);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    Object extractParameter(Parameter parameter, RQ request, Class<?> parameterClass) {
        if (parameter.isAnnotationPresent(Param.class)) {
            final Param param = parameter.getAnnotation(Param.class);
            return request.param(param.value())
                    .map(value -> mapToParameterType(value, parameterClass))
                    .orElse(null);
        } else if (parameter.isAnnotationPresent(Body.class)) {
            return extractBody(parameterClass, request);
        } else {
            return null;
        }
    }

    private Object mapToParameterType(String value, Class<?> type) {
        if (type.isAssignableFrom(Long.class)) {
            return Long.parseLong(value);
        } else if (type.isAssignableFrom(Integer.class)) {
            return Integer.parseInt(value);
        } else if (type.isAssignableFrom(String.class)) {
            return value;
        }
        throw new RuntimeException("Parameter type " + type.getCanonicalName() + " is not yet implemented");
    }

    protected Object extractBody(Class<?> parameterClass, RQ request) {
        final Object parameterInstance = ClassUtils.newInstance(parameterClass);
        Arrays.stream(parameterClass.getDeclaredFields())
                .forEach(field -> {
                    try {
                        ClassUtils.findSetter(parameterClass, field.getName())
                                .invoke(parameterInstance,
                                        request.param(field.getName()).orElse(null));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
        return parameterInstance;
    }

    public abstract RS process(M methodReturn, Map<String, Object> properties);
}
