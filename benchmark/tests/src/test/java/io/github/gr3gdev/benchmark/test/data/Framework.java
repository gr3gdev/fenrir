package io.github.gr3gdev.benchmark.test.data;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum Framework {
    SPRING("benchmark-spring", 9001, Pattern.compile("Started SpringApp in (.*) seconds")),
    QUARKUS("benchmark-quarkus", 9002, Pattern.compile("started in (.*)s\\.")),
    FENRIR("benchmark-fenrir", 9003, Pattern.compile("started on port [0-9]+ in (.*) seconds"));

    final String service;
    final int port;
    final Pattern startedPattern;

    Framework(String service, int port, Pattern startedPattern) {
        this.service = service;
        this.port = port;
        this.startedPattern = startedPattern;
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

    public static Framework findByService(String serviceName) {
        return Arrays.stream(values())
                .filter(f -> f.service.equals(serviceName))
                .findFirst()
                .orElse(null);
    }
}
