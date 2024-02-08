package io.github.gr3gdev.bench;

import io.github.gr3gdev.bench.data.Request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BiConsumer;

public class BenchTest {

    private BenchTest() {
        // None
    }

    public static void execute(HttpClient client, Request.Data request, int exposePort, BiConsumer<HttpResponse<String>, Long> onResponse) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + exposePort + request.path()));
        if (request.json() == null) {
            builder = builder.method(request.method(), HttpRequest.BodyPublishers.noBody());
        } else {
            builder = builder.method(request.method(), HttpRequest.BodyPublishers.ofString(request.json()));
        }
        final HttpRequest httpRequest = builder
                .header("Content-Type", "application/json")
                .build();
        try {
            final Instant start = Instant.now();
            final HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            final Duration duration = Duration.between(start, Instant.now());
            final long time = duration.toMillis();
            onResponse.accept(httpResponse, time);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
