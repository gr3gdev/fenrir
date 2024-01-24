package io.github.gr3gdev.benchmark.test.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Report {

    private final Map<Integer, Stats> stats = new LinkedHashMap<>();
    private String dockerImageSize;

    public Map<Integer, Stats> getStats() {
        return stats;
    }

    public String getDockerImageSize() {
        return dockerImageSize;
    }

    public void setDockerImageSize(String dockerImageSize) {
        this.dockerImageSize = dockerImageSize;
    }

    public record Response(String code, String body, String time) {
    }

    public record Stats(String startedTime, List<RequestStats> requestStats) {
    }

    public record RequestStats(Data.Request request, Response response) {
    }
}
