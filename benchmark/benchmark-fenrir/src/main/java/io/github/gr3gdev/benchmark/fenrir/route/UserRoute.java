package io.github.gr3gdev.benchmark.fenrir.route;

import io.github.gr3gdev.benchmark.domain.User;
import io.github.gr3gdev.benchmark.fenrir.dao.UserRepository;
import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafPlugin;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafResponse;

import java.util.Map;

@Route(plugin = ThymeleafPlugin.class)
public class UserRoute {
    private final UserRepository userRepository;

    public UserRoute(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Listener(path = "/index")
    public ThymeleafResponse showUserList() {
        return new ThymeleafResponse("index", Map.of("users", userRepository.findAll()));
    }

    @Listener(path = "/signup")
    public ThymeleafResponse showSignUpForm(User user) {
        return new ThymeleafResponse("add-user");
    }

    @Listener(path = "/adduser", method = HttpMethod.POST)
    public ThymeleafResponse addUser(@Body User user) {
        // TODO @Valid
        userRepository.save(user);
        return new ThymeleafResponse("/index", true);
    }

    @Listener(path = "/edit/{id}")
    public ThymeleafResponse showUpdateForm(@Param("id") String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        return new ThymeleafResponse("update-user", Map.of("user", user));
    }

    @Listener(path = "/update/{id}", method = HttpMethod.POST)
    public ThymeleafResponse updateUser(@Param("id") String id, @Body User user) {
        if (user.getId() == null) {
            user.setId(id);
        }
        userRepository.save(user);
        return new ThymeleafResponse("/index", true);
    }

    @Listener(path = "/delete/{id}")
    public ThymeleafResponse deleteUser(@Param("id") String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        return new ThymeleafResponse("/index", true);
    }
}
