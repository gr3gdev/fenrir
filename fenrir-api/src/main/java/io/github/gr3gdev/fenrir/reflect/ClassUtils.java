package io.github.gr3gdev.fenrir.reflect;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {

    private ClassUtils() {
        // None
    }

    public static Constructor<?> findConstructor(Class<?> objectClass) {
        final Constructor<?>[] constructors = objectClass.getDeclaredConstructors();
        if (constructors.length == 1) {
            return constructors[0];
        } else {
            throw new RuntimeException("Only one constructor is supported for " + objectClass.getCanonicalName());
        }
    }

    @SneakyThrows
    public static Object newInstance(Class<?> objectClass, Object... parameterValues) {
        final Constructor<?> constructor = findConstructor(objectClass);
        if (parameterValues != null && parameterValues.length == constructor.getParameterCount()) {
            return constructor.newInstance(parameterValues);
        } else if (constructor.getParameterCount() > 0) {
            final List<Object> parameters = new ArrayList<>();
            for (final Class<?> parameterType : constructor.getParameterTypes()) {
                parameters.add(newInstance(parameterType));
            }
            return constructor.newInstance(parameters.toArray());
        } else {
            return constructor.newInstance();
        }
    }

    @SneakyThrows
    public static Method findGetter(Class<?> clazz, String fieldName) {
        return clazz.getDeclaredMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
    }

    @SneakyThrows
    public static Method findSetter(Class<?> clazz, String fieldName) {
        return clazz.getDeclaredMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
    }
}
