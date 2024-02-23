package io.github.gr3gdev.sample.repository;

import io.github.gr3gdev.common.thymeleaf.User;
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
