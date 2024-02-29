package io.github.gr3gdev.benchmark.spring.dao;

import io.github.gr3gdev.common.thymeleaf.Prefs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrefRepository extends JpaRepository<Prefs, Long> {
}
