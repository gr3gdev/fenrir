package io.github.gr3gdev.fenrir.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Map;

public final class JPAManager {

    private static EntityManagerFactory entityManagerFactory;

    private JPAManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("fenrir-jpa");
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    static <E> CriteriaQuery<E> select(Class<E> domainClass) {
        return select(domainClass, Map.of());
    }

    static <E> CriteriaQuery<E> select(Class<E> domainClass, Map<String, Object> filters) {
        final EntityManager em = JPAManager.getEntityManager();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<E> query = cb.createQuery(domainClass);
        final Root<E> root = query.from(domainClass);
        CriteriaQuery<E> res = query.select(root);
        for (final Map.Entry<String, Object> entry : filters.entrySet()) {
            res = res.where(cb.equal(root.get(entry.getKey()), entry.getValue()));
        }
        return res;
    }

    static <E> CriteriaDelete<E> delete(Class<E> domainClass, Map<String, Object> filters) {
        final EntityManager em = JPAManager.getEntityManager();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaDelete<E> query = cb.createCriteriaDelete(domainClass);
        final Root<E> root = query.from(domainClass);
        CriteriaDelete<E> res = query;
        for (final Map.Entry<String, Object> entry : filters.entrySet()) {
            res = res.where(cb.equal(root.get(entry.getKey()), entry.getValue()));
        }
        return res;
    }
}
