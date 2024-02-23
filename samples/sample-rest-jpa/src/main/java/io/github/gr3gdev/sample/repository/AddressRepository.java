package io.github.gr3gdev.sample.repository;

import io.github.gr3gdev.common.jpa.Address;
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
