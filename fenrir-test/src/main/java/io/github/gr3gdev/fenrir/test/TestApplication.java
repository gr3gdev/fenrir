package io.github.gr3gdev.fenrir.test;

import io.github.gr3gdev.fenrir.FenrirApplication;
import lombok.Data;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class for manipulations with the application.
 */
public class TestApplication {
    private final TestData testData;
    private final HttpClient client;

    TestApplication(Class<?> classApp) {
        this.testData = new TestData();
        this.client = HttpClient.newBuilder().build();
        Thread.ofPlatform().start(() -> FenrirApplication.run(classApp, startupEvent -> {
            testData.setPort(startupEvent.port());
            testData.setStatus(startupEvent.status());
        }));
    }

    /**
     * Stop the application in testing.
     */
    void stop() {
        this.testData.status.set(false);
    }

    @SuppressWarnings("BusyWait")
    void waitStarted(long timeoutInSeconds) {
        final Instant start = Instant.now();
        try {
            while (this.testData.status == null || !this.testData.status.get()) {
                Thread.sleep(200);
                if (Duration.between(start, Instant.now()).toSeconds() > timeoutInSeconds) {
                    throw new RuntimeException("Timeout");
                }
            }
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> execute(Request request) {
        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + this.testData.getPort() + request.getPath()))
                .method(request.getMethod().name().toUpperCase(), Optional.ofNullable(request.getBody())
                        .map(HttpRequest.BodyPublishers::ofString)
                        .orElse(HttpRequest.BodyPublishers.noBody()))
                .header("Content-Type", Optional.ofNullable(request.getContentType())
                        .orElse("application/x-www-form-urlencoded"))
                .timeout(Duration.ofSeconds(10))
                .build();
        try {
            return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    static class TestData {
        private int port;
        private AtomicBoolean status;
    }
}
