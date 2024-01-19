package io.github.gr3gdev.benchmark.test;

import io.github.gr3gdev.benchmark.SuiteExtension;
import io.github.gr3gdev.benchmark.test.data.Data;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith(SuiteExtension.class)
public abstract class AbstractTest {

    public abstract Framework getFramework();

    private void executeRequest(int port, Data.Request request, HttpClient client, Report report) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + request.path));
        if (request.data == null) {
            builder = builder.method(request.method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder = builder.method(request.method, HttpRequest.BodyPublishers.ofString(request.data));
        }
        final HttpRequest httpRequest = builder.build();
        try {
            final HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            report.addResponse(request, httpResponse);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Order(1)
    @Test
    void create(Map<Framework, Report> reports, HttpClient client) {
        Data.CREATES.forEach(request -> executeRequest(getFramework().getPort(), request, client, reports.get(getFramework())));
    }

    @Order(2)
    @Test
    void update(Map<Framework, Report> reports, HttpClient client) {
        Data.UPDATES.forEach(request -> executeRequest(getFramework().getPort(), request, client, reports.get(getFramework())));
    }

    @Order(3)
    @Test
    void findAll(Map<Framework, Report> reports, HttpClient client) {
        Data.FIND_ALL.forEach(request -> executeRequest(getFramework().getPort(), request, client, reports.get(getFramework())));
    }

    @Order(4)
    @Test
    void findById(Map<Framework, Report> reports, HttpClient client) {
        Data.FIND_BY_ID.forEach(request -> executeRequest(getFramework().getPort(), request, client, reports.get(getFramework())));
    }

    @Order(5)
    @Test
    void deleteById(Map<Framework, Report> reports, HttpClient client) {
        Data.DELETE_BY_ID.forEach(request -> executeRequest(getFramework().getPort(), request, client, reports.get(getFramework())));
    }
}
