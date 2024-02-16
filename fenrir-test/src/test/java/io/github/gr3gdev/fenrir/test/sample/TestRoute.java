package io.github.gr3gdev.fenrir.test.sample;

import io.github.gr3gdev.fenrir.annotation.Body;
import io.github.gr3gdev.fenrir.annotation.Listener;
import io.github.gr3gdev.fenrir.annotation.Param;
import io.github.gr3gdev.fenrir.annotation.Route;
import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.http.HttpStatus;
import io.github.gr3gdev.fenrir.json.plugin.JsonPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route(plugin = JsonPlugin.class, path = "/test")
public class TestRoute {

    private final List<Custom> data = new ArrayList<>();

    public record Custom(String name, String value) {

    }

    @Listener(path = "/")
    public List<Custom> findAll() {
        return data;
    }

    @Listener(path = "/{name}")
    public Optional<Custom> findByName(@Param("name") String name) {
        return data.stream()
                .filter(d -> d.name().equals(name))
                .findFirst();
    }

    @Listener(path = "/", method = HttpMethod.POST, responseCode = HttpStatus.CREATED)
    public Custom create(@Body(contentType = "application/json") Custom custom) {
        data.add(custom);
        return custom;
    }

    @Listener(path = "/", method = HttpMethod.PUT)
    public Custom update(@Body(contentType = "application/json") Custom custom) {
        deleteByName(custom.name());
        data.add(custom);
        return custom;
    }

    @Listener(path = "/{name}", method = HttpMethod.DELETE, responseCode = HttpStatus.NO_CONTENT)
    public void deleteByName(@Param("name") String name) {
        data.removeIf(d -> d.name().equals(name));
    }

}
