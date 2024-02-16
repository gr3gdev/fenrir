package io.github.gr3gdev.fenrir.test;

import io.github.gr3gdev.fenrir.http.HttpMethod;
import io.github.gr3gdev.fenrir.test.sample.MyApp;
import io.github.gr3gdev.fenrir.test.sample.TestRoute;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.logging.LogManager;
import java.util.stream.IntStream;

@ExtendWith(FenrirExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestExtension {
    @App(MyApp.class)
    TestApplication testApplication;

    @BeforeAll
    public static void beforeAll() {
        try {
            LogManager.getLogManager().readConfiguration(TestExtension.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read logging.properties", e);
        }
    }

    @Order(1)
    @Test
    void callFindAll() {
        final HttpResponse<String> response = testApplication.execute(Request.builder()
                .method(HttpMethod.GET)
                .path("/test/")
                .build());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[]", response.body());
    }

    @Order(2)
    @Test
    void callFindByName() {
        final HttpResponse<String> response = testApplication.execute(Request.builder()
                .method(HttpMethod.GET)
                .path("/test/c1")
                .build());
        Assertions.assertEquals(204, response.statusCode());
        Assertions.assertEquals("", response.body());
    }

    @Order(3)
    @Test
    void callCreate() {
        final HttpResponse<String> response = testApplication.execute(Request.builder()
                .contentType("application/json")
                .method(HttpMethod.POST)
                .path("/test/")
                .body(new TestRoute.Custom("c1", "custom 1"))
                .build());
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("{\"name\":\"c1\",\"value\":\"custom 1\"}", response.body());
    }

    @Order(4)
    @Test
    void callUpdate() {
        final HttpResponse<String> response = testApplication.execute(Request.builder()
                .contentType("application/json")
                .method(HttpMethod.PUT)
                .path("/test/")
                .body(new TestRoute.Custom("c1", "custom 1 updated"))
                .build());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("{\"name\":\"c1\",\"value\":\"custom 1 updated\"}", response.body());
    }

    @Order(5)
    @Test
    void callFindAllAgain() {
        final HttpResponse<String> response = testApplication.execute(Request.builder()
                .method(HttpMethod.GET)
                .path("/test/")
                .build());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals("[{\"name\":\"c1\",\"value\":\"custom 1 updated\"}]", response.body());
    }

    @Test
    void notFound() {
        final HttpResponse<String> response = testApplication.execute(Request.builder()
                .method(HttpMethod.GET)
                .path("/unknown")
                .build());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    void multiple() {
        IntStream.range(1, 11).forEach(index -> {
            final HttpResponse<String> response = testApplication.execute(Request.builder()
                    .method(HttpMethod.GET)
                    .path("/test" + index + "/")
                    .build());
            Assertions.assertEquals(200, response.statusCode());
            Assertions.assertEquals("\"" + index + "\"", response.body());
        });
    }
}
