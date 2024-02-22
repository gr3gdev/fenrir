package io.github.gr3gdev.benchmark.test.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

public enum Framework {
    SPRING("Spring", "benchmark-spring", 9001, Pattern.compile("Started SpringApp in (.*) seconds"),
            ".*Started SpringApp in .*"
    ),
    FENRIR("Fenrir", "benchmark-fenrir", 9003, Pattern.compile("started on port [0-9]+ in (.*) seconds"),
            ".*started on port .*"
    );

    final String name;
    final String service;
    final int port;
    final Pattern startedPattern;
    final String containerStarted;

    Framework(String name, String service, int port, Pattern startedPattern, String containerStarted) {
        this.name = name;
        this.service = service;
        this.port = port;
        this.startedPattern = startedPattern;
        this.containerStarted = containerStarted;
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
