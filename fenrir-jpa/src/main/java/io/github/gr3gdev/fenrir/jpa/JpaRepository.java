package io.github.gr3gdev.fenrir.jpa;

import java.util.List;

import static io.github.gr3gdev.fenrir.jpa.JPAManager.entityManager;

/**
 * Interface for JPA.
 *
 * @param <E> the entity
 * @param <K> the type of the primary key
 */
public interface JpaRepository<E, K> extends JpaCrudRepository<E, K> {

    /**
     * Execute a select query.
     *
     * @param qlString the query
     * @return List of entity
     */
    default List<E> select(String qlString) {
        return entityManager()
                .createQuery(qlString, getDomainClass())
                .getResultList();
    }

    /**
     * Execute an update query.
     *
     * @param qlString the query
     * @return count of lines updated
     */
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
