package io.github.gr3gdev.fenrir.samples.security.service;

import io.github.gr3gdev.fenrir.samples.security.bean.User;
import io.github.gr3gdev.fenrir.samples.security.repository.UserRepository;
import io.github.gr3gdev.fenrir.security.service.SecurityService;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

public class UserService implements SecurityService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.create("test", "Pass123");
    }

    @SneakyThrows
    private byte[] hashPassword(String password) {
        final MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    public void create(String username, String password) {
        final User user = new User();
        user.setUsername(username);
        user.setPassword(hashPassword(password));
        userRepository.save(user);
    }

    public boolean isAuthenticated(String username, String password) {
        final List<User> users = userRepository.select("select u from users u where u.username=:username and u.password=:password",
                Map.of(
                        "username", username,
                        "password", hashPassword(password)
                ));
        return !users.isEmpty();
    }
}
