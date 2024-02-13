package io.github.gr3gdev.benchmark.fenrir.route;

import io.github.gr3gdev.benchmark.domain.Country;
import io.github.gr3gdev.benchmark.fenrir.dao.CountryRepository;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;

@Route(plugin = JsonPlugin.class, path = "/country")
public class CountryRest extends AbstractRest<Country> {
    public CountryRest(CountryRepository repository) {
        super(repository);
    }
}
