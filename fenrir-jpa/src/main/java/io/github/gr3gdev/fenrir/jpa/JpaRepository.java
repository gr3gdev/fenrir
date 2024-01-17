package io.github.gr3gdev.fenrir.jpa;

import java.util.List;

import static io.github.gr3gdev.fenrir.jpa.JPAManager.entityManager;

public interface JpaRepository<E, K> extends JpaCrudRepository<E, K> {

    default List<E> select(String qlString) {
        return entityManager()
                .createQuery(qlString, getDomainClass())
                .getResultList();
    }

    default int update(String qlString) {
        try {
            entityManager().getTransaction().begin();
            return entityManager()
                    .createQuery(qlString, getDomainClass())
                    .executeUpdate();
        } finally {
            entityManager().getTransaction().commit();
        }
    }

}
