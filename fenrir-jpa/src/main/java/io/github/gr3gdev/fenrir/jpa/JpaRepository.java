package io.github.gr3gdev.fenrir.jpa;

import java.util.List;

public interface JpaRepository<E extends Class<?>, K extends Class<?>> extends JpaCrudRepository<E, K> {

    default List<E> select(String qlString) {
        return JPAManager.getEntityManager()
                .createQuery(qlString, getDomainClass())
                .getResultList();
    }

    default int update(String qlString) {
        return JPAManager.getEntityManager()
                .createQuery(qlString, getDomainClass())
                .executeUpdate();
    }

}
