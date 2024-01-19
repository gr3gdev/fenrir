package io.github.gr3gdev.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import org.junit.jupiter.api.extension.*;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SuiteExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource, ParameterResolver {

    private static boolean init = false;
    public static Map<Framework, Report> reports;
    private static HttpClient client;

    private static String measureDockerImagesSize(String service) {
        return CommandUtils.execute(List.of("docker", "image", "ls",
                "--filter", "reference=gr3gdev/" + service,
                "--format", "json"));
    }

    private static Float measureStartedTime(String service, Pattern pattern) {
        final String logs = CommandUtils.execute(List.of("docker-compose", "logs", service));
        final Instant started = Instant.now();
        while (!pattern.matcher(logs).find()) {
            if (Duration.between(started, Instant.now()).toSeconds() > 30) {
                throw new RuntimeException("Starting timeout for " + service);
            }
            try {
                //noinspection BusyWait
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return Float.parseFloat(pattern.matcher(logs).group(1));
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!init) {
            // Init once for all tests
            init = true;

            client = HttpClient.newBuilder()
                    .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
                    .build();
            reports = new HashMap<>();
            Arrays.stream(Framework.values())
                    .forEach(f -> reports.put(f, new Report()));

            final ObjectMapper mapper = new ObjectMapper();
            // Docker image size
            reports.forEach((framework, report) -> {
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
            reports.forEach((framework, report) -> {
                final String service = framework.getService();
                report.setStartedTime(measureStartedTime(service, framework.getStartedPattern()));
            });
        }
    }

    @Override
    public void close() {
        // After all tests
        client.close();
        CommandUtils.execute(List.of("docker-compose", "rm", "-fs"));
        System.out.println(reports);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        final Class<?> type = parameterContext.getParameter().getType();
        return type.isAssignableFrom(reports.getClass())
                || type.isAssignableFrom(client.getClass());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        final Class<?> type = parameterContext.getParameter().getType();
        if (type.isAssignableFrom(reports.getClass())) {
            return reports;
        } else if (type.isAssignableFrom(client.getClass())) {
            return client;
        }
        throw new ParameterResolutionException("Invalid parameter : " + parameterContext.getParameter().getName());
    }
}
