package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.common.jpa.City;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.sample.repository.CityRepository;

@Route(plugin = JsonPlugin.class, path = "/city")
public class CityRestRoute extends AbstractRestRoute<City> {
    public CityRestRoute(CityRepository repository) {
        super(repository);
    }
}
