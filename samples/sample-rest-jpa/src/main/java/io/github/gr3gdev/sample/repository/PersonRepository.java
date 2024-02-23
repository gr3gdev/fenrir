package io.github.gr3gdev.sample.repository;

import io.github.gr3gdev.common.jpa.Person;
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
