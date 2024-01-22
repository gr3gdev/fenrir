package io.github.gr3gdev.benchmark.quarkus.dao;

import io.github.gr3gdev.benchmark.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
