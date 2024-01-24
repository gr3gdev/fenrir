package io.github.gr3gdev.benchmark.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gr3gdev.benchmark.TestSuite;
import io.github.gr3gdev.benchmark.test.data.Data;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import io.github.gr3gdev.benchmark.test.parameterized.IteratorSource;
import io.github.gr3gdev.benchmark.test.utils.CommandUtils;
import io.github.gr3gdev.benchmark.test.utils.RequestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractTest {

    private static final Network NETWORK = Network.newNetwork();

    private static DatabaseContainer database;

    private FrameworkContainer service;
    private ToStringConsumer logService;

    static class DatabaseContainer extends PostgreSQLContainer<DatabaseContainer> {
        DatabaseContainer() {
            super(DockerImageName.parse("postgres:12-alpine"));
            this.withUsername("bench_user");
            this.withPassword("bench_pass");
            this.withDatabaseName("benchmark");
            this.withInitScript("create_database.sql");
            this.withNetwork(NETWORK);
            this.withNetworkAliases("database");
        }
    }

    static class FrameworkContainer extends GenericContainer<FrameworkContainer> {
        FrameworkContainer(Framework framework) {
            super(DockerImageName.parse("gr3gdev/" + framework.getService()));
            this.withExposedPorts(framework.getPort());
            this.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS));
            this.waitingFor(Wait.forLogMessage(framework.getContainerStarted(), 1));
            this.withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig())
                    .withMemory(650L * 1024 * 1024)
                    .withMemorySwap(0L)
                    .withCpuCount(1L)
                    .withAutoRemove(true));
            this.withNetwork(NETWORK);
            this.withNetworkAliases(framework.getService());
        }
    }

    @BeforeAll
    public static void startDatabase() {
        ToStringConsumer logDatabase = new ToStringConsumer();
        database = new DatabaseContainer().withLogConsumer(logDatabase);
        try {
            database.start();
        } catch (ContainerLaunchException exc) {
            System.out.println(logDatabase.toString(StandardCharsets.UTF_8));
            throw exc;
        }
    }

    @AfterAll
    public static void stopDatabase() {
        database.stop();
    }

    protected abstract Framework getFramework();

    private void measureStartedTime(int index) {
        final Report report = TestSuite.reports.get(getFramework());
        final String service = getFramework().getService();
        final Pattern pattern = getFramework().getStartedPattern();
        String logs = logService.toString(StandardCharsets.UTF_8);
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
                System.out.println(logService.toString(StandardCharsets.UTF_8));
                stop();
                throw new RuntimeException("Unable to find started time for " + service + "\n" + logs);
            }
            logs = logService.toString(StandardCharsets.UTF_8);
            matcher = pattern.matcher(logs);
        }
        try {
            report.getStats().put(index, new Report.Stats(matcher.group(1), new LinkedList<>()));
        } catch (IllegalStateException e) {
            throw new RuntimeException("Error with pattern [" + pattern + "]\n" + logs);
        }
    }

    private String measureDockerImagesSize() {
        final ObjectMapper mapper = new ObjectMapper();
        final String json = CommandUtils.execute(List.of("docker", "image", "ls",
                "--filter", "reference=gr3gdev/" + getFramework().getService(),
                "--format", "json"));
        try {
            final JsonNode node = mapper.readTree(json);
            return node.get("Size").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void start() {
        this.logService = new ToStringConsumer();
        this.service = new FrameworkContainer(getFramework())
                .withLogConsumer(logService)
                .withEnv("DATABASE_URL", "jdbc:postgresql://database:5432/benchmark");
        try {
            service.start();
        } catch (ContainerLaunchException exc) {
            System.out.println(logService.toString(StandardCharsets.UTF_8));
            throw exc;
        }
        TestSuite.reports.get(getFramework()).setDockerImageSize(measureDockerImagesSize());
    }

    @AfterEach
    public void stop() {
        service.stop();
    }

    @ParameterizedTest(name = "{displayName} {arguments}")
    @IteratorSource(10)
    @DisplayName("Execute benchmark")
    void benchmark(int index) {
        measureStartedTime(index);
        final Framework framework = getFramework();
        final int exposePort = service.getMappedPort(getFramework().getPort());
        Data.CREATES.forEach(request -> RequestUtils.executeRequest(index, request, framework, exposePort));
        Data.UPDATES.forEach(request -> RequestUtils.executeRequest(index, request, framework, exposePort));
        Data.FIND_ALL.forEach(request -> RequestUtils.executeRequest(index, request, framework, exposePort));
        Data.FIND_BY_ID.forEach(request -> RequestUtils.executeRequest(index, request, framework, exposePort));
        Data.DELETE_BY_ID.forEach(request -> RequestUtils.executeRequest(index, request, framework, exposePort));
    }
}
