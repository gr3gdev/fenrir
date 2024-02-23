package io.github.gr3gdev.sample.repository;

import io.github.gr3gdev.common.jpa.Country;
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
