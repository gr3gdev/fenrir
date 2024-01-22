package io.github.gr3gdev.benchmark.fenrir.dao;

import io.github.gr3gdev.benchmark.domain.City;
import io.github.gr3gdev.fenrir.jpa.JpaRepository;

public class CityRepository implements JpaRepository<City, Long> {
    @Override
    public Class<City> getDomainClass() {
        return City.class;
    }

    @Override
    public Class<Long> getIdClass() {
        return Long.class;
    }
}
