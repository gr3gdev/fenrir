package io.github.gr3gdev.fenrir.samples.jpa;

import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Account;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Animal;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Book;
import io.github.gr3gdev.fenrir.samples.jpa.bean.Dog;
import io.github.gr3gdev.fenrir.samples.jpa.route.AccountRestRoute;
import io.github.gr3gdev.fenrir.samples.jpa.route.AnimalRestRoute;
import io.github.gr3gdev.fenrir.samples.jpa.route.BookRestRoute;
import io.github.gr3gdev.fenrir.samples.jpa.route.DogRestRoute;

@FenrirConfiguration(modes = {HttpMode.class}, plugins = {JsonPlugin.class, JpaPlugin.class})
@HttpConfiguration(routes = {AccountRestRoute.class, AnimalRestRoute.class, BookRestRoute.class, DogRestRoute.class})
@JpaConfiguration(entitiesClass = {Account.class, Animal.class, Book.class, Dog.class})
public class JpaRest {
    public static void main(String[] args) {
        FenrirApplication.run(JpaRest.class);
    }
}
