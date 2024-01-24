package io.github.gr3gdev.benchmark.test.data;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum Framework {
    SPRING("benchmark-spring", 9001, Pattern.compile("Started SpringApp in (.*) seconds"), ".*Started SpringApp in .*"),
    QUARKUS("benchmark-quarkus", 9002, Pattern.compile("started in (.*)s\\."), ".*started in .*"),
    FENRIR("benchmark-fenrir", 9003, Pattern.compile("started on port [0-9]+ in (.*) seconds"), ".*started on port .*");

    final String service;
    final int port;
    final Pattern startedPattern;
    final String containerStarted;

    Framework(String service, int port, Pattern startedPattern, String containerStarted) {
        this.service = service;
        this.port = port;
        this.startedPattern = startedPattern;
        this.containerStarted = containerStarted;
    }

    public String getService() {
        return service;
    }

    public int getPort() {
        return port;
    }

    public Pattern getStartedPattern() {
        return startedPattern;
    }

    public String getContainerStarted() {
        return containerStarted;
    }

    public static Framework findByService(String serviceName) {
        return Arrays.stream(values())
                .filter(f -> f.service.equals(serviceName))
                .findFirst()
                .orElse(null);
    }
}
