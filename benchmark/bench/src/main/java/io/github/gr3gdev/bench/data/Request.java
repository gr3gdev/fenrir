package io.github.gr3gdev.bench.data;

import lombok.Getter;

import java.util.List;

@Getter
public enum Request {
    CREATE(1, List.of(
            new Data("/country/", "POST", "{\"id\":250, \"name\": \"England_\"}"),
            new Data("/city/", "POST", "{\"id\":40000,\"name\":\"London\"}"),
            new Data("/address/", "POST", "{\"id\":180000,\"name\":\"Baker Street\"}"),
            new Data("/person/", "POST", "{\"id\":1,\"firstName\":\"Tim\",\"lastName\":\"Shoes\"}")
    )),
    UPDATE(2, List.of(
            new Data("/country/", "PUT", "{\"id\":250, \"name\": \"England\"}"),
            new Data("/city/", "PUT", "{\"id\":40000,\"name\":\"London\",\"country\":{\"id\":250,\"name\":\"England\"}}"),
            new Data("/address/", "PUT", "{\"id\":180000,\"name\":\"Baker Street\",\"city\":{\"id\":40000,\"name\":\"London\",\"country\":{\"id\":250,\"name\":\"England\"}}}"),
            new Data("/person/", "PUT", "{\"id\":1,\"firstName\":\"Tim\",\"lastName\":\"Shoes\",\"addresses\":[{\"id\":180000,\"name\":\"Baker Street\",\"city\":{\"id\":40000,\"name\":\"London\",\"country\":{\"id\":250,\"name\":\"England\"}}}]}")
    )),
    FIND_ALL(3, List.of(
            new Data("/country/", "GET", null),
            new Data("/city/", "GET", null),
            new Data("/address/", "GET", null),
            new Data("/person/", "GET", null)
    )),
    FIND_BY_ID(4, List.of(
            new Data("/country/1", "GET", null),
            new Data("/city/1", "GET", null),
            new Data("/address/1", "GET", null),
            new Data("/person/1", "GET", null)
    )),
    DELETE(5, List.of(
            new Data("/person/1", "DELETE", null),
            new Data("/address/180000", "DELETE", null),
            new Data("/city/40000", "DELETE", null),
            new Data("/country/250", "DELETE", null)
    ));

    private final int order;
    private final List<Data> data;

    Request(int order, List<Data> data) {
        this.order = order;
        this.data = data;
    }

    public record Data(String path, String method, String json) {
        public String name() {
            return method() + path().replace("/", "");
        }

        @Override
        public String toString() {
            return method + " " + path;
        }
    }
}
