package io.github.gr3gdev.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import io.github.gr3gdev.bench.data.Request;
import io.github.gr3gdev.benchmark.test.data.chart.Bar;
import io.github.gr3gdev.benchmark.test.data.chart.BarChart;
import io.github.gr3gdev.benchmark.test.data.chart.LineChart;
import io.github.gr3gdev.benchmark.test.utils.CommandUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private BarChart dockerImageSizeChart() {
        final String key = "dockerImageSize";
        final BarChart chart = new BarChart(key, Arrays.stream(Framework.values()).map(Framework::getName).toList());
        final Bar bar = new Bar("Docker image size (MB)");
        bar.setData(Arrays.stream(Framework.values())
                .map(this::measureDockerImagesSize)
                .toList());
        chart.getDatasets().add(bar);
        return chart;
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        TestSuite.client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        TestSuite.responses = new HashMap<>();
        Arrays.stream(Framework.values())
                .forEach(f -> TestSuite.responses.put(f, new HashMap<>()));

        TestSuite.report = new Report();
        TestSuite.report.getCharts().put("dockerImageSizeChart", dockerImageSizeChart());
        writeJSON();
    }

    private void writeJSON() {
        final Map<String, BarChart> averageCharts = new HashMap<>();
        TestSuite.report.getCharts().forEach((name, chart) -> {
            if (chart instanceof LineChart lineChart) {
                final BarChart barChart = new BarChart(chart.getKey(), Arrays.stream(Framework.values()).map(Framework::getName).toList());
                final Bar bar = new Bar(lineChart.getAverageLabel());
                bar.setData(lineChart.getDatasets().stream()
                        .map(d -> d.getData().stream()
                                .mapToDouble(Float::doubleValue)
                                .average()
                                .orElse(Double.NaN))
                        .toList());
                barChart.getDatasets().add(bar);
                averageCharts.put(chart.getKey() + "Chart", barChart);
            }
        });
        TestSuite.report.getCharts().putAll(averageCharts);

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
        final Map<Request.Data, HttpResponse<String>> responsesSpring = TestSuite.responses.get(Framework.SPRING);
        final Map<Request.Data, HttpResponse<String>> responsesQuarkus = TestSuite.responses.get(Framework.QUARKUS);
        final Map<Request.Data, HttpResponse<String>> responsesFenrir = TestSuite.responses.get(Framework.FENRIR);
        responsesFenrir.forEach((request, response) -> {
            Assertions.assertEquals(response.statusCode(), responsesSpring.get(request).statusCode(), "Response code is different [Fenrir -> Spring]");
            Assertions.assertEquals(response.statusCode(), responsesQuarkus.get(request).statusCode(), "Response code is different [Fenrir -> Quarkus]");
            Assertions.assertEquals(response.body(), responsesSpring.get(request).body(), "Response body is different [Fenrir -> Spring]");
            Assertions.assertEquals(response.body(), responsesQuarkus.get(request).body(), "Response body is different [Fenrir -> Quarkus]");
        });
    }
}
