package io.github.gr3gdev.bench;

import io.github.gr3gdev.bench.data.Request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class BenchTest {

    private BenchTest() {
        // None
    }

    @FunctionalInterface
    public interface OnComplete {
        void execute(HttpResponse<String> response, Long time, Exception error);
    }

    public static void execute(HttpClient client, Request.Data request, int exposePort,
                               OnComplete onResponse) {
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
        final Instant start = Instant.now();
        try {
            final HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            onResponse.execute(httpResponse, Duration.between(start, Instant.now()).toMillis(), null);
        } catch (IOException | InterruptedException e) {
            onResponse.execute(null, Duration.between(start, Instant.now()).toMillis(), e);
        }
    }
}
