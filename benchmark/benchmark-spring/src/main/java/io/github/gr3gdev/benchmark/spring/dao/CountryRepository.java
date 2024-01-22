package io.github.gr3gdev.benchmark.spring.dao;

import io.github.gr3gdev.benchmark.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
