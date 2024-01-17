package io.github.gr3gdev.fenrir.samples.jpa.route;

import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.plugin.impl.JsonPlugin;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Dog;
import io.github.gr3gdev.fenrir.samples.jpa.repository.DogRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Route(plugin = JsonPlugin.class, path = "/dog")
@RequiredArgsConstructor
public class DogRestRoute {

    private final DogRepository repository;

    @Listener(path = "/")
    public List<Dog> findAll() {
        return repository.findAll();
    }

    @Listener(path = "/{id}")
    public Optional<Dog> findById(@Param("id") Long id) {
        return repository.findById(id);
    }

    @Listener(path = "/", method = HttpMethod.POST)
    public Dog create(@Body Dog book) {
        return repository.save(book);
    }

    @Listener(path = "/", method = HttpMethod.PUT)
    public Dog update(@Body Dog book) {
        return repository.save(book);
    }

    @Listener(path = "/", method = HttpMethod.DELETE)
    public void delete(@Body Dog book) {
        repository.delete(book);
    }

    @Listener(path = "/{id}", method = HttpMethod.DELETE)
    public void deleteById(@Param("id") Long id) {
        repository.deleteById(id);
    }
}
