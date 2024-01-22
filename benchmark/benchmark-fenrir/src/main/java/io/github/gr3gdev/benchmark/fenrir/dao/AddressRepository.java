package io.github.gr3gdev.benchmark.fenrir.dao;

import io.github.gr3gdev.benchmark.domain.Address;
import io.github.gr3gdev.fenrir.jpa.JpaRepository;

public class AddressRepository implements JpaRepository<Address, Long> {
    @Override
    public Class<Address> getDomainClass() {
        return Address.class;
    }

    @Override
    public Class<Long> getIdClass() {
        return Long.class;
    }
}
