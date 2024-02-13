package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.sample.bean.Animal;
import io.github.gr3gdev.sample.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Route(plugin = JsonPlugin.class, path = "/animal")
@RequiredArgsConstructor
public class AnimalRestRoute {

    private final AnimalRepository animalRepository;

    @Listener(path = "/")
    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    @Listener(path = "/", method = HttpMethod.POST)
    public List<Animal> add(@Body Animal animal) {
        animalRepository.save(animal);
        return findAll();
    }

}
