package io.github.gr3gdev.benchmark.test;

import io.github.gr3gdev.benchmark.TestSuite;
import io.github.gr3gdev.benchmark.test.data.Data;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractTest {

    public abstract Framework getFramework();

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeRequest(int port, Data.Request request) {
        final HttpClient client = TestSuite.client;
        final Report report = TestSuite.reports.get(getFramework());
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + request.path()));
        if (request.data() == null) {
            builder = builder.method(request.method(), HttpRequest.BodyPublishers.noBody());
        } else {
            builder = builder.method(request.method(), HttpRequest.BodyPublishers.ofString(request.data()));
        }
        sleep(200);
        final HttpRequest httpRequest = builder
                .header("Content-Type", "application/json")
                .build();
        try {
            final Instant start = Instant.now();
            final HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            report.addResponse(request, httpResponse, Duration.between(start, Instant.now()));
            sleep(100);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Order(1)
    @Test
    void create() {
        Data.CREATES.forEach(request -> executeRequest(getFramework().getPort(), request));
    }

    @Order(2)
    @Test
    void update() {
        Data.UPDATES.forEach(request -> executeRequest(getFramework().getPort(), request));
    }

    @Order(3)
    @Test
    void findAll() {
        Data.FIND_ALL.forEach(request -> executeRequest(getFramework().getPort(), request));
    }

    @Order(4)
    @Test
    void findById() {
        Data.FIND_BY_ID.forEach(request -> executeRequest(getFramework().getPort(), request));
    }

    @Order(5)
    @Test
    void deleteById() {
        Data.DELETE_BY_ID.forEach(request -> executeRequest(getFramework().getPort(), request));
    }
}
