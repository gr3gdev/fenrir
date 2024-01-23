package io.github.gr3gdev.benchmark.test.data;

import java.util.List;

public class Data {

    public static final List<Request> CREATES = List.of(
            new Request("/country/", "POST", "{\"id\":1, \"name\": \"England_\"}"),
            new Request("/city/", "POST", "{\"id\":1,\"name\":\"London\"}"),
            new Request("/address/", "POST", "{\"id\":1,\"name\":\"Baker Street\"}"),
            new Request("/person/", "POST", "{\"id\":1,\"firstName\":\"Tim\",\"lastName\":\"Shoes\"}")
    );

    public static final List<Request> UPDATES = List.of(
            new Request("/country/", "PUT", "{\"id\":1, \"name\": \"England\"}"),
            new Request("/city/", "PUT", "{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}"),
            new Request("/address/", "PUT", "{\"id\":1,\"name\":\"Baker Street\",\"city\":{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}}"),
            new Request("/person/", "PUT", "{\"id\":1,\"firstName\":\"Tim\",\"lastName\":\"Shoes\",\"addresses\":[{\"id\":1,\"name\":\"Baker Street\",\"city\":{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}}]}")
    );

    public static final List<Request> FIND_ALL = List.of(
            new Request("/country/", "GET", null),
            new Request("/city/", "GET", null),
            new Request("/address/", "GET", null),
            new Request("/person/", "GET", null)
    );

    public static final List<Request> FIND_BY_ID = List.of(
            new Request("/country/1", "GET", null),
            new Request("/city/1", "GET", null),
            new Request("/address/1", "GET", null),
            new Request("/person/1", "GET", null)
    );

    public static final List<Request> DELETE_BY_ID = List.of(
            new Request("/person/1", "DELETE", null),
            new Request("/address/1", "DELETE", null),
            new Request("/city/1", "DELETE", null),
            new Request("/country/1", "DELETE", null)
    );

    public record Request(String path, String method, String data) {
        @Override
        public String toString() {
            return method + " " + path;
        }
    }
}
