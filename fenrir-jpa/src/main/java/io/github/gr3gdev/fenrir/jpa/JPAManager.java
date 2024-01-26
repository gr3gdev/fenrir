package io.github.gr3gdev.fenrir.jpa;

import io.github.gr3gdev.fenrir.jpa.plugin.JpaConfiguration;
import io.github.gr3gdev.fenrir.reflect.ClassUtils;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manager for JPA.
 */
public final class JPAManager {

    private static FenrirEntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    private JPAManager() {
        // None
    }

    /**
     * Initialize the entities.
     *
     * @param mainClass        the main class (for reflection)
     * @param fenrirProperties fenrir properties
     */
    public static void init(Class<?> mainClass, Properties fenrirProperties) {
        if (entityManagerFactory == null) {
            final JpaConfiguration jpaConfiguration = mainClass.getAnnotation(JpaConfiguration.class);
            if (jpaConfiguration == null) {
                throw new RuntimeException("Missing @JpaConfiguration annotation on the main class : " + mainClass.getCanonicalName());
            }
            entityManagerFactory = new FenrirEntityManagerFactory(Arrays.asList(jpaConfiguration.entitiesClass()), fenrirProperties);
            entityManager = entityManagerFactory.getEntityManager();
        }
    }

    /**
     * @return EntityManager
     */
    public static EntityManager entityManager() {
        return entityManager;
    }

    static <E> CriteriaQuery<E> selectFromClass(Class<E> domainClass) {
        return selectFromClass(domainClass, Map.of());
    }

    static <E> CriteriaQuery<E> selectFromClass(Class<E> domainClass, Map<String, Object> filters) {
        final EntityManager em = JPAManager.entityManager();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<E> query = cb.createQuery(domainClass);
        final Root<E> root = query.from(domainClass);
        CriteriaQuery<E> res = query.select(root);
        for (final Map.Entry<String, Object> entry : filters.entrySet()) {
            res = res.where(cb.equal(root.get(entry.getKey()), entry.getValue()));
        }
        return res;
    }

    static <E> CriteriaDelete<E> deleteFromClass(Class<E> domainClass, Map<String, Object> filters) {
        final EntityManager em = JPAManager.entityManager();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaDelete<E> query = cb.createCriteriaDelete(domainClass);
        final Root<E> root = query.from(domainClass);
        CriteriaDelete<E> res = query;
        for (final Map.Entry<String, Object> entry : filters.entrySet()) {
            res = res.where(cb.equal(root.get(entry.getKey()), entry.getValue()));
        }
        return res;
    }

    private static boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class)
                || field.isAnnotationPresent(EmbeddedId.class);
    }

    private static <E, K, R> R executeGetterWithId(Class<E> domainClass, Class<K> idClass,
                                                   Function<List<Field>, R> functionIdClass,
                                                   Function<List<Field>, R> functionIdOrEmbeddedId) {
        final List<Field> fields = getIdFields(domainClass, idClass);
        if (domainClass.isAnnotationPresent(IdClass.class)) {
            return functionIdClass.apply(fields);
        } else if (fields.size() == 1) {
            return functionIdOrEmbeddedId.apply(fields);
        }
        // Invalid
        throw new RuntimeException("Missing primary key annotation : @Id, @IdClass, @EmbeddedId");
    }

    @SuppressWarnings("unchecked")
    static <E, K> K getIdValue(Class<E> domainClass, Class<K> idClass, E entity) {
        return executeGetterWithId(domainClass, idClass,
                fields -> {
                    // Annotation : @IdClass
                    final Class<?> entityIdClass = domainClass.getAnnotation(IdClass.class).value();
                    final Map<String, Object> fieldsValues = fields.stream()
                            .collect(Collectors.toMap(Field::getName, f -> {
                                try {
                                    return ClassUtils.findGetter(domainClass, f.getName()).invoke(entity);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                    final LinkedList<Object> parameters = Arrays.stream(ClassUtils.findConstructor(entityIdClass).getParameters())
                            .map(p -> fieldsValues.get(p.getName()))
                            .collect(Collectors.toCollection(LinkedList::new));
                    return (K) ClassUtils.newInstance(entityIdClass, parameters);
                },
                fields -> {
                    // Annotation : @Id or @EmbeddedId
                    final Field field = fields.getFirst();
                    try {
                        return (K) ClassUtils.findGetter(domainClass, field.getName()).invoke(entity);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    static <E, K> Map<String, Object> getPrimaryKeyFilter(Class<E> domainClass, K idValue) {
        return executeGetterWithId(domainClass, idValue.getClass(),
                fields -> {
                    // Annotation : @IdClass
                    final Class<?> idClass = domainClass.getAnnotation(IdClass.class).value();
                    return fields.stream().collect(Collectors.toMap(Field::getName, f -> {
                        try {
                            return ClassUtils.findGetter(idClass, f.getName()).invoke(idValue);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }));
                },
                fields -> {
                    // Annotation : @Id or @EmbeddedId
                    final Field field = fields.getFirst();
                    if (field.isAnnotationPresent(Id.class)) {
                        return Map.of(field.getName(), idValue);
                    } else {
                        if (!field.getType().isAnnotationPresent(Embeddable.class)) {
                            throw new RuntimeException("Missing @Embeddable on " + field.getType().getCanonicalName());
                        }
                        return Arrays.stream(field.getType().getDeclaredFields())
                                .collect(Collectors.toMap(f -> field.getName() + "." + f.getName(), f -> {
                                    try {
                                        return ClassUtils.findGetter(field.getType(), f.getName()).invoke(idValue);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        throw new RuntimeException(e);
                                    }
                                }));
                    }
                });
    }

    private static <E, K> List<Field> getIdFields(Class<E> domainClass, Class<K> idClass) {
        return Arrays.stream(domainClass.getDeclaredFields())
                .filter(f -> isIdField(f) && f.getType().isAssignableFrom(idClass))
                .toList();
    }
}
