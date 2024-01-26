package io.github.gr3gdev.benchmark.test.data;

import java.util.Arrays;
import java.util.regex.Pattern;

public enum Framework {
    SPRING("Spring", "benchmark-spring", 9001, Pattern.compile("Started SpringApp in (.*) seconds"),
            ".*Started SpringApp in .*", "rgb(200,0,0)"),
    QUARKUS("Quarkus", "benchmark-quarkus", 9002, Pattern.compile("started in (.*)s\\."),
            ".*started in .*", "rgb(0,0,200)"),
    FENRIR("Fenrir", "benchmark-fenrir", 9003, Pattern.compile("started on port [0-9]+ in (.*) seconds"),
            ".*started on port .*", "rgb(0,200,0)");

    final String name;
    final String service;
    final int port;
    final Pattern startedPattern;
    final String containerStarted;
    final String color;

    Framework(String name, String service, int port, Pattern startedPattern, String containerStarted, String color) {
        this.name = name;
        this.service = service;
        this.port = port;
        this.startedPattern = startedPattern;
        this.containerStarted = containerStarted;
        this.color = color;
    }

    public String getName() {
        return name;
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

    public String getColor() {
        return color;
    }

    public static Framework findByService(String serviceName) {
        return Arrays.stream(values())
                .filter(f -> f.service.equals(serviceName))
                .findFirst()
                .orElse(null);
    }

}
