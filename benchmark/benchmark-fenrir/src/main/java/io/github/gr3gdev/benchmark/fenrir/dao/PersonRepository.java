package io.github.gr3gdev.benchmark.fenrir.dao;

import io.github.gr3gdev.benchmark.domain.Person;
import io.github.gr3gdev.fenrir.jpa.JpaRepository;

public class PersonRepository implements JpaRepository<Person, Long> {
    @Override
    public Class<Person> getDomainClass() {
        return Person.class;
    }

    @Override
    public Class<Long> getIdClass() {
        return Long.class;
    }
}
