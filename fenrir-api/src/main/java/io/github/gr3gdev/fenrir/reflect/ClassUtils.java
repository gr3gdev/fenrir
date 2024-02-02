package io.github.gr3gdev.fenrir.reflect;

import lombok.SneakyThrows;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utils for class manipulations.
 */
public final class ClassUtils {

    /**
     * Cache for object instantiate with default constructor.
     */
    private static final Map<Class<?>, Object> instanceCache = new HashMap<>();

    private ClassUtils() {
        // None
    }

    /**
     * Find the constructor (by reflection).
     *
     * @param objectClass the object class
     * @return Constructor
     */
    public static Constructor<?> findConstructor(Class<?> objectClass) {
        final Constructor<?>[] constructors = objectClass.getConstructors();
        if (objectClass.isInterface()) {
            throw new RuntimeException("Cannot instantiate the interface " + objectClass.getCanonicalName());
        } else if (constructors.length == 1) {
            return constructors[0];
        } else {
            throw new RuntimeException("Only one constructor is supported for " + objectClass.getCanonicalName());
        }
    }

    /**
     * Find generic classes of the method's parameters (by reflection).
     *
     * @param method         the method
     * @param genericClasses this generic classes of the class (E : Class<E>)
     * @return Map (parameter name : parameter class)
     */
    public static Map<String, Class<?>> findGenericClasses(Method method, Map<String, Class<?>> genericClasses) {
        final LinkedList<String> genericParameterTypes = Arrays.stream(method.getGenericParameterTypes())
                .map(Type::getTypeName)
                .collect(Collectors.toCollection(LinkedList::new));
        final Iterator<String> iterator = genericParameterTypes.iterator();
        return Arrays.stream(method.getParameters())
                .collect(Collectors.toMap(Parameter::getName, p -> {
                    Class<?> pClass = genericClasses.get(iterator.next());
                    if (pClass == null) {
                        pClass = p.getType();
                    }
                    return pClass;
                }));
    }

    /**
     * Find generic classes of the class (by reflection).
     *
     * @param objectClass the object class
     * @return Map (generic name : generic real class)
     */
    public static Map<String, Class<?>> findGenericClasses(Class<?> objectClass) {
        final Class<?> superClass = objectClass.getSuperclass();
        final TypeVariable<? extends Class<?>>[] typeParameters = superClass.getTypeParameters();
        final Type superClassType = objectClass.getGenericSuperclass();
        if (superClassType instanceof ParameterizedType superClassParameterizedType) {
            final Type[] typeArguments = superClassParameterizedType.getActualTypeArguments();
            final Map<String, Class<?>> parametersRealType = new HashMap<>();
            for (int idx = 0; idx < typeArguments.length; idx++) {
                parametersRealType.put(typeParameters[idx].getName(), (Class<?>) typeArguments[idx]);
            }
            return parametersRealType;
        }
        return Map.of();
    }

    /**
     * Create a new instance of an object (by reflection).
     *
     * @param objectClass     the object class
     * @param parameterValues the parameter values
     * @return Object
     */
    @SneakyThrows
    public static Object newInstance(Class<?> objectClass, Object... parameterValues) {
        final Constructor<?> constructor = findConstructor(objectClass);
        if (parameterValues != null && parameterValues.length == constructor.getParameterCount()) {
            if (parameterValues.length == 0) {
                return newInstanceCache(objectClass, constructor);
            } else {
                return constructor.newInstance(parameterValues);
            }
        } else if (constructor.getParameterCount() > 0) {
            final List<Object> parameters = new ArrayList<>();
            for (final Class<?> parameterType : constructor.getParameterTypes()) {
                parameters.add(newInstance(parameterType));
            }
            return constructor.newInstance(parameters.toArray());
        } else {
            return newInstanceCache(objectClass, constructor);
        }
    }

    private static Object newInstanceCache(Class<?> objectClass, Constructor<?> constructor) {
        return instanceCache.computeIfAbsent(objectClass, k -> {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Find the getter method (by reflection).
     *
     * @param objectClass the object class
     * @param fieldName   the field name
     * @return Method
     */
    @SneakyThrows
    public static Method findGetter(Class<?> objectClass, String fieldName) {
        return objectClass.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
    }

    /**
     * Find the setter method (by reflection).
     *
     * @param objectClass the object class
     * @param field       the field
     * @return Method
     */
    @SneakyThrows
    public static Method findSetter(Class<?> objectClass, Field field) {
        final String fieldName = field.getName();
        final String setter = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return objectClass.getMethod(setter, field.getType());
    }
}
