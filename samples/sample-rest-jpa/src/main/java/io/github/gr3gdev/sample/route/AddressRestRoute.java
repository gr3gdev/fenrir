package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.common.jpa.Address;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.sample.repository.AddressRepository;

@Route(plugin = JsonPlugin.class, path = "/address")
public class AddressRestRoute extends AbstractRestRoute<Address> {
    public AddressRestRoute(AddressRepository repository) {
        super(repository);
    }
}
