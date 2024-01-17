package io.github.gr3gdev.fenrir.samples.jpa.repository;

import io.github.gr3gdev.fenrir.jpa.JpaRepository;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Dog;

public class DogRepository implements JpaRepository<Dog, Long> {
    @Override
    public Class<Dog> getDomainClass() {
        return Dog.class;
    }

    @Override
    public Class<Long> getIdClass() {
        return Long.class;
    }
}
