package io.github.gr3gdev.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import io.github.gr3gdev.benchmark.test.data.chart.Chart;
import io.github.gr3gdev.benchmark.test.utils.CommandUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class BeforeAfterSuiteListener implements TestExecutionListener {

    private Double measureDockerImagesSize(Framework framework) {
        final ObjectMapper mapper = new ObjectMapper();
        final String json = CommandUtils.execute(List.of("docker", "image", "ls",
                "--filter", "reference=gr3gdev/" + framework.getService(),
                "--format", "json"));
        try {
            final JsonNode node = mapper.readTree(json);
            final String size = node.get("Size").asText();
            return Double.parseDouble(size.substring(0, size.toUpperCase().indexOf("MB")));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Chart dockerImageSizeChart() {
        final String key = "dockerImageSize";
        final Chart chart = new Chart(key,
                "charts-css column show-heading show-labels show-primary-axis show-data-axes data-spacing-10",
                "Docker image's size");
        Arrays.stream(Framework.values())
                .forEach(framework -> {
                    final Double value = measureDockerImagesSize(framework);
                    chart.addDataset(framework.getName(), new Chart.Value(value, value + "MB",
                            framework.getName() + " size",
                            "The docker's image for " + framework.getName() + " is " + value + "MB"));
                });
        return chart;
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        System.setOut(new PrintStream(System.out) {
            @Override
            public void println(@Nullable String x) {
                super.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now()) + " : " + x);
            }
        });

        TestSuite.client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        Arrays.stream(Framework.values())
                .forEach(f -> f.getDirectory(true));

        TestSuite.report = new Report();
        TestSuite.report.getCharts().put("dockerImageSizeChart", dockerImageSizeChart());
        writeJSON();
    }

    private void writeJSON() {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("report.json"), TestSuite.report);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        // After all tests
        TestSuite.client.close();

        // HTML report
        writeJSON();

        // Compare responses
        /*List.of(256L, 512L, 1000L).forEach(mem ->
                IntStream.range(0, 10)
                        .forEach(index -> Arrays.stream(Request.values())
                                .map(Request::getData)
                                .flatMap(Collection::stream)
                                .forEach(r -> {
                                    final File requestFileSpring = TestSuite.getRequestFile(Framework.SPRING, r, index, mem);
                                    final File requestFileQuarkus = TestSuite.getRequestFile(Framework.QUARKUS, r, index, mem);
                                    final File requestFileFenrir = TestSuite.getRequestFile(Framework.FENRIR, r, index, mem);
                                    try {
                                        assertEquals(Files.readAllLines(requestFileSpring.toPath()), Files.readAllLines(requestFileFenrir.toPath()), "Response is different between Spring & Fenrir");
                                        assertEquals(Files.readAllLines(requestFileSpring.toPath()), Files.readAllLines(requestFileQuarkus.toPath()), "Response is different between Spring & Quarkus");
                                        assertEquals(Files.readAllLines(requestFileQuarkus.toPath()), Files.readAllLines(requestFileFenrir.toPath()), "Response is different between Quarkus & Fenrir");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }))
        );*/
    }
}
