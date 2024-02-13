package io.github.gr3gdev.sample;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.sample.bean.Account;
import io.github.gr3gdev.sample.bean.Animal;
import io.github.gr3gdev.sample.bean.Book;
import io.github.gr3gdev.sample.bean.Dog;
import io.github.gr3gdev.sample.route.AccountRestRoute;
import io.github.gr3gdev.sample.route.AnimalRestRoute;
import io.github.gr3gdev.sample.route.BookRestRoute;
import io.github.gr3gdev.sample.route.DogRestRoute;

import java.io.IOException;
import java.util.logging.LogManager;

@FenrirConfiguration(modes = {HttpMode.class}, plugins = {JsonPlugin.class, JpaPlugin.class})
@HttpConfiguration(routes = {AccountRestRoute.class, AnimalRestRoute.class, BookRestRoute.class, DogRestRoute.class})
@JpaConfiguration(entitiesClass = {Account.class, Animal.class, Book.class, Dog.class})
public class JpaApp {
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(JpaApp.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read logging.properties", e);
        }
        FenrirApplication.run(JpaApp.class);
    }
}