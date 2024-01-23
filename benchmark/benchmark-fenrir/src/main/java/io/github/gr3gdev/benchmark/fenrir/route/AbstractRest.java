package io.github.gr3gdev.benchmark.fenrir.route;

import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.jpa.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRest<E> {
    protected final JpaRepository<E, Long> repository;

    public AbstractRest(JpaRepository<E, Long> repository) {
        this.repository = repository;
    }

    @Listener(path = "/", contentType = "application/json")
    public List<E> findAll() {
        return repository.findAll();
    }

    @Listener(path = "/{id}", contentType = "application/json")
    public Optional<E> findById(@Param("id") Long id) {
        return repository.findById(id);
    }

    @Listener(path = "/", method = HttpMethod.POST, contentType = "application/json")
    public E create(@Body(contentType = "application/json") E bean) {
        return repository.save(bean);
    }

    @Listener(path = "/", method = HttpMethod.PUT, contentType = "application/json")
    public E update(@Body(contentType = "application/json") E bean) {
        return repository.save(bean);
    }

    @Listener(path = "/", method = HttpMethod.DELETE, contentType = "application/json", responseCode = HttpStatus.NO_CONTENT)
    public void delete(@Body(contentType = "application/json") E bean) {
        repository.delete(bean);
    }

    @Listener(path = "/{id}", method = HttpMethod.DELETE, contentType = "application/json", responseCode = HttpStatus.NO_CONTENT)
    public void deleteById(@Param("id") Long id) {
        repository.deleteById(id);
    }
}
