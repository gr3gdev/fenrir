package io.github.gr3gdev.benchmark.quarkus.resources;

import io.github.gr3gdev.benchmark.quarkus.bean.Person;
import io.github.gr3gdev.benchmark.quarkus.dao.PersonRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;
import java.util.Optional;

@Path("/person")
public class PersonResource extends AbstractResource<Person> {

    protected PersonResource(PersonRepository repository) {
        super(repository);
    }
}
