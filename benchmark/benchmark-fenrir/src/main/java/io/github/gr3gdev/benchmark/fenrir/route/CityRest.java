package io.github.gr3gdev.benchmark.fenrir.route;

import io.github.gr3gdev.benchmark.fenrir.bean.City;
import io.github.gr3gdev.benchmark.fenrir.dao.CityRepository;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.plugin.impl.JsonPlugin;

@Route(plugin = JsonPlugin.class, path = "/city")
public class CityRest extends AbstractRest<City> {
    public CityRest(CityRepository repository) {
        super(repository);
    }
}
