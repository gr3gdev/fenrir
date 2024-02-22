package io.github.gr3gdev.benchmark.fenrir.dao;

import io.github.gr3gdev.benchmark.domain.User;
import io.github.gr3gdev.fenrir.jpa.JpaRepository;

public class UserRepository implements JpaRepository<User, String> {
    @Override
    public Class<User> getDomainClass() {
        return User.class;
    }

    @Override
    public Class<String> getIdClass() {
        return String.class;
    }
}
