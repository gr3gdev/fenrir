package io.github.gr3gdev.fenrir.jpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JpaCrudRepository<E extends Class<?>, K extends Class<?>> {

    Class<E> getDomainClass();

    Map<String, Object> getPrimaryKeyFilter(K id);

    default List<E> findAll() {
        return JPAManager.getEntityManager()
                .createQuery(JPAManager.select(getDomainClass()))
                .getResultList();
    }

    default Optional<E> findById(K id) {
        return Optional.of(JPAManager.getEntityManager()
                .createQuery(JPAManager.select(getDomainClass(), getPrimaryKeyFilter(id)))
                .getSingleResult());
    }

    default E save(E entity) {
        return JPAManager.getEntityManager().merge(entity);
    }

    default void delete(E entity) {
        JPAManager.getEntityManager().detach(entity);
    }

    default int deleteById(K id) {
        return JPAManager.getEntityManager().createQuery(
                        JPAManager.delete(getDomainClass(), getPrimaryKeyFilter(id)))
                .executeUpdate();
    }
}
