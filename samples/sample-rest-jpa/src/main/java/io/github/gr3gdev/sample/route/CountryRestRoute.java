package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.common.jpa.Country;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.sample.repository.CountryRepository;

@Route(plugin = JsonPlugin.class, path = "/country")
public class CountryRestRoute extends AbstractRestRoute<Country> {
    public CountryRestRoute(CountryRepository repository) {
        super(repository);
    }
}
