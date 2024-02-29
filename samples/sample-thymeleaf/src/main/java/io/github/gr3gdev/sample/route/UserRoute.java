package io.github.gr3gdev.sample.route;

import io.github.gr3gdev.common.thymeleaf.Prefs;
import io.github.gr3gdev.common.thymeleaf.User;
import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.thymeleaf.ThymeleafResponse;
import io.github.gr3gdev.fenrir.thymeleaf.validation.ThymeleafValidationPlugin;
import io.github.gr3gdev.fenrir.thymeleaf.validation.ValidationResult;
import io.github.gr3gdev.sample.repository.PrefRepository;
import io.github.gr3gdev.sample.repository.UserRepository;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Route(plugin = ThymeleafValidationPlugin.class)
public class UserRoute {
    private final UserRepository userRepository;
    private final PrefRepository prefRepository;

    public UserRoute(UserRepository userRepository, PrefRepository prefRepository) {
        this.userRepository = userRepository;
        this.prefRepository = prefRepository;
    }

    private ThymeleafResponse construct(String page, String key, Object value) {
        final Locale locale = prefRepository.findById(1L)
                .map(Prefs::getLocale)
                .orElse(Locale.UK);
        return new ThymeleafResponse(page, Map.of(
                key, value,
                "locale", locale,
                "locales", List.of(Locale.UK, Locale.FRANCE, Locale.GERMANY)),
                locale
        );
    }

    private ThymeleafResponse redirectIndex() {
        return new ThymeleafResponse("/index", true);
    }

    private User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }

    @Listener(path = "/index", method = HttpMethod.POST)
    public ThymeleafResponse update(@Param("locale") String lang) {
        final Locale currentLocale = Locale.of(lang);
        final Prefs prefs = new Prefs();
        prefs.setId(1L);
        prefs.setLocale(currentLocale);
        prefRepository.save(prefs);
        return redirectIndex();
    }

    @Listener(path = "/index")
    public ThymeleafResponse showUserList() {
        return construct("index", "users", userRepository.findAll());
    }

    @Listener(path = "/signup")
    public ThymeleafResponse showSignUpForm() {
        return construct("add-user", "user", new User());
    }

    @Listener(path = "/adduser", method = HttpMethod.POST)
    public ThymeleafResponse addUser(@Valid @Body User user, ValidationResult result) {
        if (!result.violations().isEmpty()) {
            return construct("add-user", "user", user);
        }
        userRepository.save(user);
        return redirectIndex();
    }

    @Listener(path = "/edit/{id}")
    public ThymeleafResponse showUpdateForm(@Param("id") String id) {
        return construct("update-user", "user", findUserById(id));
    }

    @Listener(path = "/update/{id}", method = HttpMethod.POST)
    public ThymeleafResponse updateUser(@Param("id") String id, @Valid @Body User user, ValidationResult result) {
        if (!result.violations().isEmpty()) {
            return construct("update-user", "user", user);
        }
        if (user.getId() == null) {
            user.setId(id);
        }
        userRepository.save(user);
        return redirectIndex();
    }

    @Listener(path = "/delete/{id}")
    public ThymeleafResponse deleteUser(@Param("id") String id) {
        userRepository.delete(findUserById(id));
        return redirectIndex();
    }
}
