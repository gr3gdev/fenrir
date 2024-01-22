package io.github.gr3gdev.benchmark.fenrir.dao;

import io.github.gr3gdev.benchmark.domain.Country;
import io.github.gr3gdev.fenrir.jpa.JpaRepository;

public class CountryRepository implements JpaRepository<Country, Long> {
    @Override
    public Class<Country> getDomainClass() {
        return Country.class;
    }

    @Override
    public Class<Long> getIdClass() {
        return Long.class;
    }
}
