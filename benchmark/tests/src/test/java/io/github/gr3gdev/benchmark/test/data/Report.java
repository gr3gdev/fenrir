package io.github.gr3gdev.benchmark.test.data;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Report {

    private final Map<Data.Request, Response> responses = new LinkedHashMap<>();
    private String dockerImageSize;
    private String startedTime;

    public void addResponse(Data.Request request, HttpResponse<String> httpResponse, Duration duration) {
        responses.put(request, new Response(
                Optional.ofNullable(httpResponse).map(r -> String.valueOf(r.statusCode())).orElse("ERROR"),
                Optional.ofNullable(httpResponse).map(HttpResponse::body).orElse("ERROR"),
                Optional.ofNullable(duration).map(d -> String.valueOf(d.toMillis())).orElse("ERROR")));
    }

    public void setDockerImageSize(String dockerImageSize) {
        this.dockerImageSize = dockerImageSize;
    }

    public void setStartedTime(String startedTime) {
        this.startedTime = startedTime;
    }

    public Map<Data.Request, Response> getResponses() {
        return responses;
    }

    public String getDockerImageSize() {
        return dockerImageSize;
    }

    public String getStartedTime() {
        return startedTime;
    }

    public record Response(String code, String body, String time) {
    }
}
