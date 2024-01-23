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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class BeforeAfterSuiteListener implements TestExecutionListener {

    private final File reportDir = new File("build", "benchmark");

    private final String dockerCompose = reportDir.getAbsolutePath() + "/docker-compose";

    public BeforeAfterSuiteListener() {
        if (!reportDir.exists()) {
            try {
                Files.createDirectory(reportDir.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String measureDockerImagesSize(String service) {
        return CommandUtils.execute(List.of("docker", "image", "ls",
                "--filter", "reference=gr3gdev/" + service,
                "--format", "json"));
    }

    private String measureStartedTime(String service, Pattern pattern) {
        String logs = CommandUtils.execute(List.of(dockerCompose, "logs", service));
        final Instant started = Instant.now();
        Matcher matcher = pattern.matcher(logs);
        while (!matcher.find()) {
            try {
                //noinspection BusyWait
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Duration.between(started, Instant.now()).toSeconds() > 30) {
                writeLogs();
                stop();
                throw new RuntimeException("Starting timeout for " + service + "\n" + logs);
            }
            logs = CommandUtils.execute(List.of(dockerCompose, "logs", service));
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
        prepareDockerCompose();

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
        start();

        // Measure started time
        TestSuite.reports.forEach((framework, report) -> {
            final String service = framework.getService();
            report.setStartedTime(measureStartedTime(service, framework.getStartedPattern()));
        });
    }

    private void prepareDockerCompose() {
        try {
            final URL downloadURL = URI.create("https://github.com/docker/compose/releases/download/v2.24.2/docker-compose-linux-x86_64").toURL();
            try (final ReadableByteChannel readableByteChannel = Channels.newChannel(downloadURL.openStream());
                 final FileOutputStream fileOutputStream = new FileOutputStream(new File(reportDir, "docker-compose"));
                 final FileChannel fileChannel = fileOutputStream.getChannel()) {
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            }
            CommandUtils.execute(List.of("chmod", "+x", dockerCompose));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        try {
            // After all tests
            TestSuite.client.close();
            reporting(TestSuite.reports);
        } finally {
            stop();
        }
    }

    private void start() {
        CommandUtils.execute(List.of(dockerCompose, "up", "-d"));
    }

    private void stop() {
        CommandUtils.execute(List.of(dockerCompose, "rm", "-fs"));
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
                    Assertions.assertEquals(getResponseCode(springResponse), getResponseCode(fenrirResponse), "Response code is not the same between : Spring and Fenrir");
                    Assertions.assertEquals(getResponseCode(quarkusResponse), getResponseCode(fenrirResponse), "Response code is not the same between : Quarkus and Fenrir");
                    Assertions.assertEquals(getResponseCode(springResponse), getResponseCode(quarkusResponse), "Response code is not the same between : Spring and Quarkus");
                    Assertions.assertEquals(getResponseBody(springResponse), getResponseBody(fenrirResponse), "Response body is not the same between : Spring and Fenrir");
                    Assertions.assertEquals(getResponseBody(quarkusResponse), getResponseBody(fenrirResponse), "Response body is not the same between : Quarkus and Fenrir");
                    Assertions.assertEquals(getResponseBody(springResponse), getResponseBody(quarkusResponse), "Response body is not the same between : Spring and Quarkus");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("Report : " + report.getAbsolutePath());
            writeLogs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeLogs() {
        for (final Framework framework : Framework.values()) {
            final String logs = CommandUtils.execute(List.of(dockerCompose, "logs", framework.getService()));
            try {
                Files.writeString(new File(reportDir, framework.getService() + ".log").toPath(), logs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
