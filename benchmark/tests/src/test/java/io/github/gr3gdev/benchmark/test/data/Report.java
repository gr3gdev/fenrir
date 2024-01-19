package io.github.gr3gdev.benchmark.test.data;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Report {

    final Map<Data.Request, HttpResponse<String>> responses = new HashMap<>();
    private String dockerImageSize;
    private Float startedTime;

    public void addResponse(Data.Request request, HttpResponse<String> httpResponse) {
        responses.put(request, httpResponse);
    }

    public void setDockerImageSize(String dockerImageSize) {
        this.dockerImageSize = dockerImageSize;
    }

    public void setStartedTime(Float startedTime) {
        this.startedTime = startedTime;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("- Docker image size : ").append(dockerImageSize).append("\n");
        builder.append("- Started time : ").append(startedTime).append(" seconds\n");
        responses.forEach((req, res) -> {
            builder.append("- Request ")
                    .append(req.method)
                    .append(" ")
                    .append(req.path)
                    .append("\n")
                    .append(" (")
                    .append(res.statusCode())
                    .append(") ")
                    .append(res.body())
                    .append("\n");
        });
        return builder.toString();
    }

}
