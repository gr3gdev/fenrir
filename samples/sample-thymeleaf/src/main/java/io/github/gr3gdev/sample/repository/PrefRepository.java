package io.github.gr3gdev.sample.repository;

import io.github.gr3gdev.common.thymeleaf.Prefs;
import io.github.gr3gdev.fenrir.jpa.JpaRepository;

public class PrefRepository implements JpaRepository<Prefs, Long> {
    @Override
    public Class<Prefs> getDomainClass() {
        return Prefs.class;
    }

    @Override
    public Class<Long> getIdClass() {
        return Long.class;
    }
}
