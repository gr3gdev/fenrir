package io.github.gr3gdev.sample;

import io.github.gr3gdev.common.jpa.Address;
import io.github.gr3gdev.common.jpa.City;
import io.github.gr3gdev.common.jpa.Country;
import io.github.gr3gdev.common.jpa.Person;
import io.github.gr3gdev.fenrir.FenrirApplication;
import io.github.gr3gdev.fenrir.FenrirConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaConfiguration;
import io.github.gr3gdev.fenrir.jpa.plugin.JpaPlugin;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;
import io.github.gr3gdev.fenrir.runtime.HttpConfiguration;
import io.github.gr3gdev.fenrir.runtime.HttpMode;
import io.github.gr3gdev.sample.route.AddressRestRoute;
import io.github.gr3gdev.sample.route.CityRestRoute;
import io.github.gr3gdev.sample.route.CountryRestRoute;
import io.github.gr3gdev.sample.route.PersonRestRoute;

import java.io.IOException;
import java.util.logging.LogManager;

@FenrirConfiguration(modes = {HttpMode.class}, plugins = {JsonPlugin.class, JpaPlugin.class})
@HttpConfiguration(routes = {AddressRestRoute.class, CityRestRoute.class, CountryRestRoute.class, PersonRestRoute.class})
@JpaConfiguration(entitiesClass = {Address.class, City.class, Country.class, Person.class})
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