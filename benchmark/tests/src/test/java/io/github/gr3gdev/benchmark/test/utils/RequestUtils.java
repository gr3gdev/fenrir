package io.github.gr3gdev.benchmark.test.utils;

import io.github.gr3gdev.benchmark.TestSuite;
import io.github.gr3gdev.benchmark.test.data.Data;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class RequestUtils {

    private RequestUtils() {
        // None
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeRequest(int index, Data.Request request, Framework framework, int exposePort) {
        final HttpClient client = TestSuite.client;
        final Report report = TestSuite.reports.get(framework);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + exposePort + request.path()));
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
            final Duration duration = Duration.between(start, Instant.now());
            final Report.Stats stats = report.getStats().get(index);
            if (stats != null) {
                stats.requestStats().add(new Report.RequestStats(request, new Report.Response(
                        Optional.ofNullable(httpResponse).map(r -> String.valueOf(r.statusCode())).orElse("ERROR"),
                        Optional.ofNullable(httpResponse).map(HttpResponse::body).orElse("ERROR"),
                        Optional.ofNullable(duration).map(d -> String.valueOf(d.toMillis())).orElse("ERROR"))));
            }
            sleep(100);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
