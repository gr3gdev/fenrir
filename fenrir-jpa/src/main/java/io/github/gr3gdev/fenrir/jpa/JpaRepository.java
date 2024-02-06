package io.github.gr3gdev.fenrir.jpa;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
     * @param qlString   the query
     * @param parameters the parameters
     * @return List of entity
     */
    default List<E> select(String qlString, Map<String, Object> parameters) {
        final TypedQuery<E> query = entityManager()
                .createQuery(qlString, getDomainClass());
        parameters.forEach(query::setParameter);
        return query.getResultList();
    }

    /**
     * Execute an update query.
     *
     * @param qlString   the query
     * @param parameters the parameters
     * @return count of lines updated
     */
    default int update(String qlString, Map<String, Object> parameters) {
        return executeInTransaction(() -> {
            final TypedQuery<E> query = entityManager()
                    .createQuery(qlString, getDomainClass());
            parameters.forEach(query::setParameter);
            return query.executeUpdate();
        });
    }

}
