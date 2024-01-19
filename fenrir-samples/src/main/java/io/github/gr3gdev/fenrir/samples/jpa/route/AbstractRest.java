package io.github.gr3gdev.fenrir.samples.jpa.route;

import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.jpa.JpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AbstractRest<E, K> {

    protected final JpaRepository<E, K> repository;

    @Listener(path = "/")
    public List<E> findAll() {
        return repository.findAll();
    }

    @Listener(path = "/", method = HttpMethod.POST, responseCode = HttpStatus.CREATED)
    public E create(@Body E entity) {
        return repository.save(entity);
    }

    @Listener(path = "/", method = HttpMethod.PUT)
    public E update(@Body E entity) {
        return repository.save(entity);
    }

    @Listener(path = "/", method = HttpMethod.DELETE)
    public void delete(@Body E entity) {
        repository.delete(entity);
    }
}
