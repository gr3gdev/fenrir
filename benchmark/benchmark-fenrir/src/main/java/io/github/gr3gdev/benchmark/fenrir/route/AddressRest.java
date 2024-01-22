package io.github.gr3gdev.benchmark.fenrir.route;

import io.github.gr3gdev.benchmark.domain.Address;
import io.github.gr3gdev.benchmark.fenrir.dao.AddressRepository;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.plugin.impl.JsonPlugin;

@Route(plugin = JsonPlugin.class, path = "/address")
public class AddressRest extends AbstractRest<Address> {
    public AddressRest(AddressRepository repository) {
        super(repository);
    }
}
