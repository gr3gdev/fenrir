package io.github.gr3gdev.benchmark.quarkus.resources;

import io.github.gr3gdev.benchmark.quarkus.bean.Country;
import io.github.gr3gdev.benchmark.quarkus.dao.CountryRepository;
import jakarta.ws.rs.Path;

@Path("/country")
public class CountryResource extends AbstractResource<Country> {
    protected CountryResource(CountryRepository repository) {
        super(repository);
    }
}
