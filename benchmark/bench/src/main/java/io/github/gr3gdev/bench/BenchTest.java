package io.github.gr3gdev.bench;

import io.github.gr3gdev.bench.data.Request;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class BenchTest {

    private BenchTest() {
        // None
    }

    @FunctionalInterface
    public interface OnComplete {
        void execute(HttpResponse<InputStream> response, Long time, Exception error);
    }

    public static List<Request> load() {
        try (final InputStream input = BenchTest.class.getResourceAsStream("/benchmark.properties")) {
            final Properties benchProperties = new Properties();
            benchProperties.load(input);
            final List<String> keys = benchProperties.keySet().stream()
                    .map(key -> (String) key)
                    .map(key -> key.substring("request.".length(), key.lastIndexOf(".")))
                    .distinct()
                    .toList();
            return keys.stream()
                    .map(key -> new Request(
                            Integer.parseInt(benchProperties.getProperty("request." + key + ".order")),
                            benchProperties.getProperty("request." + key + ".method"),
                            benchProperties.getProperty("request." + key + ".path"),
                            benchProperties.getProperty("request." + key + ".body")
                    ))
                    .sorted(Comparator.comparing(Request::order))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void execute(HttpClient client, Request request, int exposePort,
                               OnComplete onResponse) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + exposePort + request.path()));
        if (request.body().isBlank()) {
            builder = builder.method(request.method(), HttpRequest.BodyPublishers.noBody());
        } else {
            builder = builder.method(request.method(), HttpRequest.BodyPublishers.ofString(request.body()));
        }
        final HttpRequest httpRequest = builder
                .header("Content-Type", "application/json")
                .build();
        final Instant start = Instant.now();
        try {
            final HttpResponse<InputStream> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
            onResponse.execute(httpResponse, Duration.between(start, Instant.now()).toMillis(), null);
        } catch (Exception e) {
            onResponse.execute(null, Duration.between(start, Instant.now()).toMillis(), e);
        }
    }
}
