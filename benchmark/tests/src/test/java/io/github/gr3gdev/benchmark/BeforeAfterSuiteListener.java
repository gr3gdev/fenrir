package io.github.gr3gdev.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class BeforeAfterSuiteListener implements TestExecutionListener {

    private String measureDockerImagesSize(String service) {
        return CommandUtils.execute(List.of("docker", "image", "ls",
                "--filter", "reference=gr3gdev/" + service,
                "--format", "json"));
    }

    private String measureStartedTime(String service, Pattern pattern) {
        String logs = CommandUtils.execute(List.of("docker-compose", "logs", service));
        final Instant started = Instant.now();
        Matcher matcher = pattern.matcher(logs);
        while (!matcher.find()) {
            try {
                //noinspection BusyWait
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Duration.between(started, Instant.now()).toSeconds() > 15) {
                throw new RuntimeException("Starting timeout for " + service + "\n" + logs);
            }
            logs = CommandUtils.execute(List.of("docker-compose", "logs", service));
            matcher = pattern.matcher(logs);
        }
        try {
            return matcher.group(1);
        } catch (IllegalStateException e) {
            Assertions.fail("Error with pattern [" + pattern + "]\n" + logs);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        TestSuite.client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        TestSuite.reports = new HashMap<>();
        Arrays.stream(Framework.values())
                .forEach(f -> TestSuite.reports.put(f, new Report()));

        final ObjectMapper mapper = new ObjectMapper();
        // Docker image size
        TestSuite.reports.forEach((framework, report) -> {
            final String service = framework.getService();
            final String json = measureDockerImagesSize(service);
            try {
                final JsonNode node = mapper.readTree(json);
                report.setDockerImageSize(node.get("Size").asText());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        // Start containers
        CommandUtils.execute(List.of("docker-compose", "up", "-d"));

        // Measure started time
        TestSuite.reports.forEach((framework, report) -> {
            final String service = framework.getService();
            report.setStartedTime(measureStartedTime(service, framework.getStartedPattern()));
        });
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        try {
            // After all tests
            TestSuite.client.close();
            reporting(TestSuite.reports);
        } finally {
            CommandUtils.execute(List.of("docker-compose", "rm", "-fs"));
        }
    }

    private String getDockerImageSize(Report report) {
        return Optional.ofNullable(report).map(Report::getDockerImageSize).orElse("?");
    }

    private String getStartedTime(Report report) {
        return Optional.ofNullable(report).map(Report::getStartedTime).orElse("?");
    }

    private String getResponseTime(Report.Response response) {
        return Optional.ofNullable(response).map(Report.Response::time).orElse("?");
    }

    private String getResponseCode(Report.Response response) {
        return Optional.ofNullable(response).map(Report.Response::code).orElse("?");
    }

    private String getResponseBody(Report.Response response) {
        return Optional.ofNullable(response).map(Report.Response::body).orElse("?");
    }

    private void reporting(Map<Framework, Report> reports) {
        final File reportDir = new File("build", "benchmark");
        if (!reportDir.exists()) {
            try {
                Files.createDirectory(reportDir.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        final File report = new File(reportDir, "report_" + Instant.now().toEpochMilli() + ".csv");
        try (FileWriter writer = new FileWriter(report)) {
            final Report springReport = TestSuite.reports.get(Framework.SPRING);
            final Report quarkusReport = TestSuite.reports.get(Framework.QUARKUS);
            final Report fenrirReport = TestSuite.reports.get(Framework.FENRIR);
            writer.write("Framework;Spring;Quarkus;Fenrir;\n");
            writer.append("Docker size;")
                    .append(getDockerImageSize(springReport)).append(";")
                    .append(getDockerImageSize(quarkusReport)).append(";")
                    .append(getDockerImageSize(fenrirReport)).append(";\n");
            writer.append("Start time (seconds);")
                    .append(getStartedTime(springReport)).append(";")
                    .append(getStartedTime(quarkusReport)).append(";")
                    .append(getStartedTime(fenrirReport)).append(";\n");
            springReport.getResponses().forEach((request, springResponse) -> {
                final Report.Response quarkusResponse = quarkusReport.getResponses().get(request);
                final Report.Response fenrirResponse = fenrirReport.getResponses().get(request);
                try {
                    writer.append(request.toString()).append(" time (ms);")
                            .append(getResponseTime(springResponse)).append(";")
                            .append(getResponseTime(quarkusResponse)).append(";")
                            .append(getResponseTime(fenrirResponse)).append(";\n");
                    writer.append(request.toString()).append(" response (code);")
                            .append(getResponseCode(springResponse)).append(";")
                            .append(getResponseCode(quarkusResponse)).append(";")
                            .append(getResponseCode(fenrirResponse)).append(";\n");
                    writer.append(request.toString()).append(" response (json);")
                            .append(getResponseBody(springResponse)).append(";")
                            .append(getResponseBody(quarkusResponse)).append(";")
                            .append(getResponseBody(fenrirResponse)).append(";\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("Report : " + report.getAbsolutePath());
            for (final Framework framework : Framework.values()) {
                final String logs = CommandUtils.execute(List.of("docker-compose", "logs", framework.getService()));
                Files.writeString(new File(reportDir, framework.getService() + ".log").toPath(), logs);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
