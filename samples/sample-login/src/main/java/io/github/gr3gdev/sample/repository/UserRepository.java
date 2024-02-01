package io.github.gr3gdev.sample.repository;

import io.github.gr3gdev.fenrir.jpa.JpaRepository;
import io.github.gr3gdev.sample.bean.User;

public class UserRepository implements JpaRepository<User, Long> {
    @Override
    public Class<User> getDomainClass() {
        return User.class;
    }

    @Override
    public Class<Long> getIdClass() {
        return Long.class;
    }
}
