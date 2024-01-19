package io.github.gr3gdev.fenrir.samples.jpa.repository;

import io.github.gr3gdev.fenrir.jpa.JpaRepository;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Animal;

public class AnimalRepository implements JpaRepository<Animal, Long> {
    @Override
    public Class<Animal> getDomainClass() {
        return Animal.class;
    }

    @Override
    public Class<Long> getIdClass() {
        return Long.class;
    }
}
