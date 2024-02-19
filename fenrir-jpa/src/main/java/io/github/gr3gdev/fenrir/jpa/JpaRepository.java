package io.github.gr3gdev.fenrir.jpa;

import io.github.gr3gdev.fenrir.jpa.page.Page;
import io.github.gr3gdev.fenrir.jpa.page.Pageable;
import io.github.gr3gdev.fenrir.jpa.page.Sort;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.gr3gdev.fenrir.jpa.JPAManager.entityManager;
import static io.github.gr3gdev.fenrir.jpa.JPAManager.selectFromClass;

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

    /**
     * Find all entities in pageable mode.
     *
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param sorts      the sorts to apply on the page
     * @return Pageable object with result
     */
    default Page<E> findAll(int pageNumber, int pageSize, Sort... sorts) {
        final int offset = pageNumber * pageSize;
        final long totalElements = count();
        final int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        final Map<String, Boolean> orders = Arrays.stream(sorts)
                .collect(Collectors.toMap(Sort::by, s -> s.direction().isAsc()));
        final List<E> result = entityManager()
                .createQuery(selectFromClass(getDomainClass(), Map.of(), orders))
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
        final boolean empty = result.isEmpty();
        final int numberOfElements = result.size();
        final boolean first = pageNumber == 0;
        final boolean last = pageNumber == totalPages - 1;
        return new Page<>(result, new Pageable(pageNumber, pageSize, sorts, offset),
                totalPages, totalElements, first, last, numberOfElements, empty);
    }

    /**
     * Count the number of elements.
     *
     * @return long
     */
    default long count() {
        final CriteriaBuilder cb = entityManager().getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        query.select(cb.count(query.from(getDomainClass())));
        return entityManager().createQuery(query).getSingleResult();
    }
}
