package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.common.jpa.Person;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.sample.repository.PersonRepository;

@Route(plugin = JsonPlugin.class, path = "/person")
public class PersonRestRoute extends AbstractRestRoute<Person> {
    public PersonRestRoute(PersonRepository repository) {
        super(repository);
    }
}
