package io.github.gr3gdev.benchmark.test.utils;

import io.github.gr3gdev.benchmark.TestSuite;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Request;
import io.github.gr3gdev.benchmark.test.data.chart.LineChart;
import io.github.gr3gdev.benchmark.test.parameterized.IteratorSource;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

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

    public static void executeRequest(IteratorSource.Iteration iteration, Request.Data request, Framework framework, int exposePort) {
        final HttpClient client = TestSuite.client;
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + exposePort + request.path()));
        if (request.json() == null) {
            builder = builder.method(request.method(), HttpRequest.BodyPublishers.noBody());
        } else {
            builder = builder.method(request.method(), HttpRequest.BodyPublishers.ofString(request.json()));
        }
        sleep(200);
        final HttpRequest httpRequest = builder
                .header("Content-Type", "application/json")
                .build();
        try {
            final Instant start = Instant.now();
            final HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            final Duration duration = Duration.between(start, Instant.now());
            final long time = duration.toMillis();

            TestSuite.responses.get(framework).put(request, httpResponse);

            final String key = "requestTimeChart" + request.name() + iteration.memory();
            ((LineChart) TestSuite.report.getCharts()
                    .computeIfAbsent(key,
                            k -> new LineChart(key, IntStream.range(1, iteration.max() + 1).mapToObj(String::valueOf).toList(), "Average request time (ms)")))
                    .save(framework, iteration, request.toString(), time);

            sleep(100);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
