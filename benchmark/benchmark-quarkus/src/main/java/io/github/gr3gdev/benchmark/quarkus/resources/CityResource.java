package io.github.gr3gdev.benchmark.quarkus.resources;

import io.github.gr3gdev.benchmark.domain.City;
import io.github.gr3gdev.benchmark.quarkus.dao.CityRepository;
import jakarta.ws.rs.Path;

@Path("/city")
public class CityResource extends AbstractResource<City> {
    protected CityResource(CityRepository repository) {
        super(repository);
    }
}
