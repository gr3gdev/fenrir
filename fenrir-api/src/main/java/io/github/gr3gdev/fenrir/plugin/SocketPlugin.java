package io.github.gr3gdev.fenrir.plugin;

import io.github.gr3gdev.fenrir.Request;
import io.github.gr3gdev.fenrir.Response;
import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.interceptor.Interceptor;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;
import io.github.gr3gdev.fenrir.validator.PluginValidator;
import io.github.gr3gdev.fenrir.validator.RouteValidator;
import io.github.gr3gdev.fenrir.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Abstract class for {@link Plugin} resolution of responses.
 *
 * @param <M>  method return type (the response)
 * @param <RQ> the request implementation
 * @param <RS> the response implementation
 */
public abstract class SocketPlugin<M, RQ extends Request, RS extends Response> implements Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketPlugin.class);

    private final Set<PluginValidator<?>> validators = new HashSet<>();

    @Override
    public final void addValidator(PluginValidator<?> validator) {
        LOGGER.trace("Add a validator : {}", validator.getClass().getCanonicalName());
        validators.add(validator);
    }

    @SuppressWarnings("unchecked")
    protected <B> List<PluginValidator<B>> findValidatorsFor(Class<B> annotationClass) {
        return this.validators.stream()
                .filter(v -> v.getSupportedClass().equals(annotationClass))
                .map(v -> (PluginValidator<B>) v)
                .toList();
    }

    /**
     * Process a request and return a response.
     *
     * @param routeClass    the route class
     * @param routeInstance the instance of the route class
     * @param method        the method called
     * @param request       the request
     * @param properties    properties for Content-Type, Http Code for response, ...
     * @param validators    the validators execute before processing the request
     * @param interceptors  list of interceptors
     * @return Response
     */
    @SuppressWarnings("unchecked")
    public final RS process(Class<?> routeClass, Object routeInstance, Method method, RQ request, Map<String, Object> properties,
                            List<RouteValidator> validators, List<Interceptor<?, RS, ?>> interceptors) {
        LOGGER.trace("Process a request : {}", routeClass.getCanonicalName());
        final Map<String, Class<?>> genericClasses = ClassUtils.findGenericClasses(routeClass);
        final Map<String, Class<?>> parameterClasses = ClassUtils.findGenericClasses(method, genericClasses);
        final List<Object> parameterValues = new LinkedList<>();
        try {
            executeValidatorByAnnotation(method, request, properties);
            for (final Parameter parameter : method.getParameters()) {
                // Validate parameters (by plugin)
                parameterValues.add(extractParameter(parameter, request, properties, parameterClasses.get(parameter.getName())));
            }
            final Object methodReturn;
            if (parameterValues.isEmpty()) {
                methodReturn = method.invoke(routeInstance);
            } else {
                final Object[] parameterValuesArray = parameterValues.toArray();
                final List<RouteValidator> validatorsToExecute = validators.stream().filter(v -> v.supports(parameterValuesArray)).toList();
                for (final RouteValidator validator : validatorsToExecute) {
                    // Validate parameters (by request)
                    validator.validate(request, properties, parameterValuesArray);
                }
                methodReturn = method.invoke(routeInstance, parameterValuesArray);
            }
            return process((M) methodReturn, properties);
        } catch (ValidatorException e) {
            final RS response = (RS) e.getResponse();
            return interceptors.stream()
                    .filter(i -> i.supports(e))
                    .findFirst()
                    .map(i -> process((M) i.replace(() -> response), properties))
                    .orElse(response);
        } catch (Exception e) {
            return processInternalError(properties, e);
        }
    }

    /**
     * Create a response when an internal error is thrown.
     *
     * @param properties properties for Content-Type, Http Code for response, ...
     * @param exception  the exception
     * @return Response
     */
    protected abstract RS processInternalError(Map<String, Object> properties, Exception exception);

    Object extractParameter(Parameter parameter, RQ request, Map<String, Object> properties, Class<?> parameterClass) throws ValidatorException {
        final Object result;
        executeValidatorByAnnotation(parameter, request, properties);
        if (parameter.isAnnotationPresent(Param.class)) {
            final Param param = parameter.getAnnotation(Param.class);
            result = request.param(param.value())
                    .map(value -> mapToParameterType(value, parameterClass))
                    .orElse(null);
        } else if (parameter.isAnnotationPresent(Body.class)) {
            result = extractBody(parameterClass, request);
        } else {
            result = null;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    <B> void executeValidatorByAnnotation(AnnotatedElement annotatedElement, RQ request,
                                          Map<String, Object> properties) throws ValidatorException {
        for (final Annotation annotation : annotatedElement.getAnnotations()) {
            for (final PluginValidator<B> validator : findValidatorsFor((Class<B>) annotation.annotationType())) {
                validator.validate(request, properties, (B) annotatedElement.getAnnotation(annotation.annotationType()));
            }
        }
    }

    private Object mapToParameterType(String value, Class<?> type) {
        if (type.isAssignableFrom(Long.class) || type.equals(long.class)) {
            return Long.parseLong(value);
        } else if (type.isAssignableFrom(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (type.isAssignableFrom(Float.class) || type.equals(float.class)) {
            return Float.parseFloat(value);
        } else if (type.isAssignableFrom(Double.class) || type.equals(double.class)) {
            return Double.parseDouble(value);
        } else if (type.isAssignableFrom(String.class)) {
            return value;
        }
        throw new RuntimeException("Parameter type " + type.getCanonicalName() + " is not yet implemented, please use String");
    }

    /**
     * Extract the parameter value in the request and convert it.
     *
     * @param parameterClass the parameter class
     * @param request        the request
     * @return Object (parameter value)
     */
    protected Object extractBody(Class<?> parameterClass, RQ request) {
        LOGGER.trace("Extract body from request and map to {}", parameterClass.getCanonicalName());
        final Object parameterInstance = ClassUtils.newInstance(parameterClass);
        Arrays.stream(parameterClass.getDeclaredFields())
                .forEach(field -> {
                    try {
                        ClassUtils.findSetter(parameterClass, field)
                                .invoke(parameterInstance,
                                        request.param(field.getName()).orElse(null));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
        return parameterInstance;
    }

    /**
     * Process the response with the method return value.
     *
     * @param methodReturn the method return value
     * @param properties   properties for Content-Type, Http Code for response, ...
     * @return Response
     */
    public abstract RS process(M methodReturn, Map<String, Object> properties);
}
