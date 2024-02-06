package io.github.gr3gdev.fenrir.jpa;

import jakarta.persistence.EntityTransaction;

import java.util.function.Supplier;

import static io.github.gr3gdev.fenrir.jpa.JPAManager.entityManager;

/**
 * Interface for transactional actions.
 */
public interface JpaTransactionalRepository {

    @FunctionalInterface
    interface Method {
        void call();
    }

    /**
     * Execute a request in a transaction.
     *
     * @param method functional interface contains the request
     */
    default void executeInTransaction(Method method) {
        final EntityTransaction transaction = entityManager().getTransaction();
        try {
            transaction.begin();
            method.call();
        } catch (Exception exc) {
            transaction.rollback();
            throw exc;
        } finally {
            transaction.commit();
        }
    }

    /**
     * Execute a request in a transaction.
     *
     * @param supplier functional interface contains the request
     * @param <R>      Type of return
     * @return count of lines updated
     */
    default <R> R executeInTransaction(Supplier<R> supplier) {
        final EntityTransaction transaction = entityManager().getTransaction();
        try {
            transaction.begin();
            return supplier.get();
        } catch (Exception exc) {
            transaction.rollback();
            throw exc;
        } finally {
            transaction.commit();
        }
    }

}
