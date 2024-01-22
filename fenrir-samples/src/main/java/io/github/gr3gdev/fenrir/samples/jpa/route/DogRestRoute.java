package io.github.gr3gdev.fenrir.samples.jpa.route;

import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Dog;
import io.github.gr3gdev.fenrir.samples.jpa.repository.DogRepository;

import java.util.Optional;

@Route(plugin = JsonPlugin.class, path = "/dog")
public class DogRestRoute extends AbstractRest<Dog, Long> {

    public DogRestRoute(DogRepository repository) {
        super(repository);
    }

    @Listener(path = "/{id}")
    public Optional<Dog> findById(@Param("id") Long id) {
        return repository.findById(id);
    }

    @Listener(path = "/{id}", method = HttpMethod.DELETE)
    public void deleteById(@Param("id") Long id) {
        repository.deleteById(id);
    }
}