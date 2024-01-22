package io.github.gr3gdev.benchmark.quarkus.resources;

import io.github.gr3gdev.benchmark.domain.Person;
import io.github.gr3gdev.benchmark.quarkus.dao.PersonRepository;
import jakarta.ws.rs.Path;

@Path("/person")
public class PersonResource extends AbstractResource<Person> {

    protected PersonResource(PersonRepository repository) {
        super(repository);
    }
}
