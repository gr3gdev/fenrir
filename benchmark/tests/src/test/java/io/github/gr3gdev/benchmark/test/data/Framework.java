package io.github.gr3gdev.benchmark.test.data;

import io.github.gr3gdev.bench.Iteration;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.regex.Pattern;

public enum Framework {
    SPRING("Spring", "benchmark-spring", 9001, Pattern.compile("Started SpringApp in (.*) seconds"),
            ".*Started SpringApp in .*",
            Map.of(
                    256L, "rgb(255,175,175)",
                    512L, "rgb(255,100,100)",
                    1000L, "rgb(255,0,0)"
            )),
    QUARKUS("Quarkus", "benchmark-quarkus", 9002, Pattern.compile("started in (.*)s\\."),
            ".*started in .*",
            Map.of(
                    256L, "rgb(175,175,255)",
                    512L, "rgb(100,100,255)",
                    1000L, "rgb(0,0,255)"
            )),
    FENRIR("Fenrir", "benchmark-fenrir", 9003, Pattern.compile("started on port [0-9]+ in (.*) seconds"),
            ".*started on port .*",
            Map.of(
                    256L, "rgb(175,255,175)",
                    512L, "rgb(100,255,100)",
                    1000L, "rgb(0,255,0)"
            ));

    final String name;
    final String service;
    final int port;
    final Pattern startedPattern;
    final String containerStarted;
    final Map<Long, String> color;

    Framework(String name, String service, int port, Pattern startedPattern, String containerStarted, Map<Long, String> color) {
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

    public String getColor(Iteration iteration) {
        return color.get(iteration.memory());
    }

    public File getDirectory(boolean init) {
        final File directory = new File("build", this.name.toLowerCase());
        if (init) {
            try {
                if (directory.exists()) {
                    Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(
                                Path file, BasicFileAttributes attrs)
                                throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
                Files.createDirectory(directory.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return directory;
    }
}
