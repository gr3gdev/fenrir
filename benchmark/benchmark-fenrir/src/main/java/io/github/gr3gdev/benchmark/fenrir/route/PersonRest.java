package io.github.gr3gdev.benchmark.fenrir.route;

import io.github.gr3gdev.benchmark.fenrir.bean.Person;
import io.github.gr3gdev.benchmark.fenrir.dao.PersonRepository;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.plugin.impl.JsonPlugin;

@Route(plugin = JsonPlugin.class, path = "/person")
public class PersonRest extends AbstractRest<Person> {
    public PersonRest(PersonRepository repository) {
        super(repository);
    }
}
