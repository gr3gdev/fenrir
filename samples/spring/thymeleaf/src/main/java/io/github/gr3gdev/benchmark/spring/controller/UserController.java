package io.github.gr3gdev.benchmark.spring.controller;

import io.github.gr3gdev.benchmark.spring.dao.PrefRepository;
import io.github.gr3gdev.benchmark.spring.dao.UserRepository;
import io.github.gr3gdev.common.thymeleaf.Prefs;
import io.github.gr3gdev.common.thymeleaf.User;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

@Controller
public class UserController {
    private final UserRepository userRepository;
    private final PrefRepository prefRepository;

    public UserController(UserRepository userRepository, PrefRepository prefRepository) {
        this.userRepository = userRepository;
        this.prefRepository = prefRepository;
    }

    User findUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }

    void addCommonParameters(Model model) {
        final Locale locale = prefRepository.findById(1L)
                .map(Prefs::getLocale)
                .orElse(Locale.UK);
        model.addAttribute("locale", locale);
        model.addAttribute("locales", List.of(Locale.UK, Locale.FRANCE, Locale.GERMANY));
    }

    @PostMapping("/index")
    public String updateLocale(@RequestParam("locale") String lang) {
        final Locale currentLocale = Locale.of(lang);
        final Prefs prefs = new Prefs();
        prefs.setId(1L);
        prefs.setLocale(currentLocale);
        prefRepository.save(prefs);
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String showUserList(Model model) {
        addCommonParameters(model);
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

    @GetMapping("/signup")
    public String showSignUpForm(User user, Model model) {
        addCommonParameters(model);
        return "add-user";
    }

    @PostMapping("/adduser")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addCommonParameters(model);
            return "add-user";
        }
        userRepository.save(user);
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") String id, Model model) {
        addCommonParameters(model);
        User user = findUser(id);
        model.addAttribute("user", user);
        return "update-user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") String id, @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addCommonParameters(model);
            user.setId(id);
            return "update-user";
        }
        userRepository.save(user);
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") String id) {
        User user = findUser(id);
        userRepository.delete(user);
        return "redirect:/index";
    }
}
