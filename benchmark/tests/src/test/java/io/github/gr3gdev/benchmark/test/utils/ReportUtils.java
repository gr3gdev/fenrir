package io.github.gr3gdev.benchmark.test.utils;

import io.github.gr3gdev.benchmark.TestSuite;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class ReportUtils {
    public static final File REPORT_DIR = new File("build", "benchmark");

    public ReportUtils() {
        // None
    }

    private static void addLine(FileWriter writer, String... args) {
        try {
            for (final String arg : args) {
                writer.append(arg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void report() {
        final Map<Framework, Report> reports = TestSuite.reports;
        reports.forEach((framework, report) -> {
            try {
                final File directory = new File(REPORT_DIR, framework.getService());
                if (!directory.exists()) {
                    Files.createDirectory(directory.toPath());
                }
                try (final FileWriter writer = new FileWriter(new File(directory, "report.log"))) {
                    writer.write(framework.getService());
                    addLine(writer, "\nDocker image size : ", report.getDockerImageSize());
                    report.getStats().forEach((index, stats) -> {
                        addLine(writer, "\n[", index.toString(), "] Started time : ", stats.startedTime());
                        stats.requestStats().forEach(requestStat -> {
                            addLine(writer, "\n[", index.toString(), "] ", requestStat.request().toString(), " time : ", requestStat.response().time());
                            final File requestDir = new File(directory, requestStat.request().name());
                            try {
                                if (!requestDir.exists()) {
                                    Files.createDirectory(requestDir.toPath());
                                }
                                try (final FileWriter responseWriter = new FileWriter(new File(requestDir, "response_" + index + ".json"))) {
                                    responseWriter.write("{\"code\": " + requestStat.response().code() + ", \"body\": " + requestStat.response().body() + "}");
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
