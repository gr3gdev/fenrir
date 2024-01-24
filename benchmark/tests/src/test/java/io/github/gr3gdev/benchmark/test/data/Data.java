package io.github.gr3gdev.benchmark.test.data;

import java.util.List;

public class Data {

    public static final List<Request> CREATES = List.of(
            new Request("create_country", "/country/", "POST", "{\"id\":1, \"name\": \"England_\"}"),
            new Request("create_city", "/city/", "POST", "{\"id\":1,\"name\":\"London\"}"),
            new Request("create_address", "/address/", "POST", "{\"id\":1,\"name\":\"Baker Street\"}"),
            new Request("create_person", "/person/", "POST", "{\"id\":1,\"firstName\":\"Tim\",\"lastName\":\"Shoes\"}")
    );

    public static final List<Request> UPDATES = List.of(
            new Request("update_country", "/country/", "PUT", "{\"id\":1, \"name\": \"England\"}"),
            new Request("update_city", "/city/", "PUT", "{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}"),
            new Request("update_address", "/address/", "PUT", "{\"id\":1,\"name\":\"Baker Street\",\"city\":{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}}"),
            new Request("update_person", "/person/", "PUT", "{\"id\":1,\"firstName\":\"Tim\",\"lastName\":\"Shoes\",\"addresses\":[{\"id\":1,\"name\":\"Baker Street\",\"city\":{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}}]}")
    );

    public static final List<Request> FIND_ALL = List.of(
            new Request("find_all_country", "/country/", "GET", null),
            new Request("find_all_city", "/city/", "GET", null),
            new Request("find_all_address", "/address/", "GET", null),
            new Request("find_all_person", "/person/", "GET", null)
    );

    public static final List<Request> FIND_BY_ID = List.of(
            new Request("find_country_by_id", "/country/1", "GET", null),
            new Request("find_city_by_id", "/city/1", "GET", null),
            new Request("find_address_by_id", "/address/1", "GET", null),
            new Request("find_person_by_id", "/person/1", "GET", null)
    );

    public static final List<Request> DELETE_BY_ID = List.of(
            new Request("delete_person", "/person/1", "DELETE", null),
            new Request("delete_address", "/address/1", "DELETE", null),
            new Request("delete_city", "/city/1", "DELETE", null),
            new Request("delete_country", "/country/1", "DELETE", null)
    );

    public record Request(String name, String path, String method, String data) {
        @Override
        public String toString() {
            return method + " " + path;
        }
    }
}
