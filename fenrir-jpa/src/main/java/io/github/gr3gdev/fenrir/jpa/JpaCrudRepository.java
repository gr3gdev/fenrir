package io.github.gr3gdev.fenrir.jpa;

import java.util.List;
import java.util.Optional;

import static io.github.gr3gdev.fenrir.jpa.JPAManager.*;

public interface JpaCrudRepository<E, K> {

    Class<E> getDomainClass();

    Class<K> getIdClass();

    default List<E> findAll() {
        return entityManager()
                .createQuery(selectFromClass(getDomainClass()))
                .getResultList();
    }

    default Optional<E> findById(K id) {
        return Optional.of(entityManager()
                .createQuery(selectFromClass(getDomainClass(), getPrimaryKeyFilter(getDomainClass(), id)))
                .getSingleResult());
    }

    default E save(E entity) {
        if (entity == null) {
            throw new RuntimeException("Cannot save a null entity");
        }
        try {
            entityManager().getTransaction().begin();
            if (getIdValue(getDomainClass(), getIdClass(), entity) == null) {
                entityManager().persist(entity);
                entityManager().flush();
                return entity;
            } else {
                return entityManager().merge(entity);
            }
        } finally {
            entityManager().getTransaction().commit();
        }
    }

    default void delete(E entity) {
        if (entity == null) {
            throw new RuntimeException("Cannot delete a null entity");
        }
        try {
            entityManager().getTransaction().begin();
            entityManager().detach(entity);
        } finally {
            entityManager().getTransaction().commit();
        }
    }

    default int deleteById(K id) {
        try {
            entityManager().getTransaction().begin();
            return entityManager().createQuery(
                            deleteFromClass(getDomainClass(), getPrimaryKeyFilter(getDomainClass(), id)))
                    .executeUpdate();
        } finally {
            entityManager().getTransaction().commit();
        }
    }
}
