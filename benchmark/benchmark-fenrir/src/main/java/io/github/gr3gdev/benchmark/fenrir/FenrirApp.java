package io.github.gr3gdev.benchmark.fenrir;

import io.github.gr3gdev.benchmark.domain.Address;
import io.github.gr3gdev.benchmark.domain.City;
import io.github.gr3gdev.benchmark.domain.Country;
import io.github.gr3gdev.benchmark.domain.Person;
import io.github.gr3gdev.benchmark.fenrir.route.AddressRest;
import io.github.gr3gdev.benchmark.fenrir.route.CityRest;
import io.github.gr3gdev.benchmark.fenrir.route.CountryRest;
import io.github.gr3gdev.benchmark.fenrir.route.PersonRest;
import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;

import java.io.IOException;
import java.util.logging.LogManager;

@FenrirConfiguration(
        plugins = {JsonPlugin.class, JpaPlugin.class},
        modes = {HttpMode.class}
)
@HttpConfiguration(routes = {AddressRest.class, CityRest.class, CountryRest.class, PersonRest.class})
@JpaConfiguration(entitiesClass = {Address.class, City.class, Country.class, Person.class})
public class FenrirApp {
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(FenrirApp.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read logging.properties", e);
        }
        FenrirApplication.run(FenrirApp.class);
    }
}
