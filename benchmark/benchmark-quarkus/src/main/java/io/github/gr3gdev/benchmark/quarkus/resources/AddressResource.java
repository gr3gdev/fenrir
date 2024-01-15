package io.github.gr3gdev.benchmark.quarkus.resources;

import io.github.gr3gdev.benchmark.quarkus.bean.Address;
import io.github.gr3gdev.benchmark.quarkus.dao.AddressRepository;
import jakarta.ws.rs.Path;

@Path("/address")
public class AddressResource extends AbstractResource<Address> {
    protected AddressResource(AddressRepository repository) {
        super(repository);
    }
}
