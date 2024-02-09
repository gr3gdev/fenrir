package io.github.gr3gdev.benchmark.test;

import io.github.gr3gdev.bench.BenchTest;
import io.github.gr3gdev.bench.Iteration;
import io.github.gr3gdev.bench.data.Request;
import io.github.gr3gdev.benchmark.TestSuite;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.chart.LineChart;
import io.github.gr3gdev.benchmark.test.parameterized.IteratorSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

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
            this.waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 2));
        }
    }

    static class FrameworkContainer extends GenericContainer<FrameworkContainer> {
        FrameworkContainer(Framework framework, long memory) {
            super(DockerImageName.parse("gr3gdev/" + framework.getService()));
            this.withExposedPorts(framework.getPort());
            this.withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS));
            this.waitingFor(Wait.forLogMessage(framework.getContainerStarted(), 1));
            // Memory : 256m
            // CPU : 1
            this.withCreateContainerCmdModifier(cmd -> Objects.requireNonNull(cmd.getHostConfig())
                    .withMemory(memory * 1024 * 1024)
                    .withMemorySwap(0L)
                    .withCpuCount(1L)
                    .withAutoRemove(true));
            this.withNetwork(NETWORK);
            this.withNetworkAliases(framework.getService());
            // JVM memory = Heap memory + Metaspace + CodeCache + (ThreadStackSize * Number of Threads) + DirectByteBuffers + Jvm-native
            // Heap memory (-Xms and -Xms)
            long heapSize = memory / 4;
            // Metaspace (-XX:MetaspaceSize and -XX:MaxMetaspaceSize)
            long metaspaceSize = memory / 4;
            // CodeCache (-XX:InitialCodeCacheSize and -XX:ReservedCodeCacheSize)
            long codeCacheSize = memory / 8;
            // ThreadStackSize (-Xss)
            this.withEnv("JAVA_OPTS", String.format("-Xms%1$dm -Xmx%1$dm -XX:MetaspaceSize=%2$dM -XX:MaxMetaspaceSize=%2$dM " +
                            "-XX:InitialCodeCacheSize=%3$dM -XX:ReservedCodeCacheSize=%3$dM -Xss%4$sk -XX:MaxRAM=%4$dm",
                    heapSize, metaspaceSize, codeCacheSize, memory));
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

    private void measureStartedTime(Iteration iteration) {
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
            final float startedTime = Float.parseFloat(matcher.group(1));
            final String key = "startedTimeChart" + iteration.memory();
            ((LineChart) TestSuite.report.getCharts()
                    .computeIfAbsent(key,
                            k -> new LineChart(key, IntStream.range(1, iteration.max() + 1).mapToObj(String::valueOf).toList(), "Average started time (seconds)")))
                    .save(getFramework(), iteration, "started time", startedTime);
        } catch (IllegalStateException e) {
            throw new RuntimeException("Error with pattern [" + pattern + "]\n" + logs);
        }
    }

    public void start(Iteration iteration) {
        this.logService = new ToStringConsumer();
        this.service = new FrameworkContainer(getFramework(), iteration.memory())
                .withLogConsumer(logService)
                .withEnv("DATABASE_URL", "jdbc:postgresql://database:5432/benchmark");
        try {
            service.start();
        } catch (ContainerLaunchException exc) {
            System.out.println(logService.toString(StandardCharsets.UTF_8));
            throw exc;
        }
        measureStartedTime(iteration);
    }

    @AfterEach
    public void stop() {
        service.stop();
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest(name = "{displayName} {arguments}")
    @IteratorSource({
            @IteratorSource.IterationConf(count = 10, memory = 256L),
            @IteratorSource.IterationConf(count = 10, memory = 512L)
    })
    @DisplayName("Execute benchmark")
    void benchmark(Iteration iteration) {
        start(iteration);
        final Framework framework = getFramework();
        final int exposePort = service.getMappedPort(getFramework().getPort());
        Arrays.stream(Request.values())
                .sorted(Comparator.comparing(Request::getOrder))
                .map(Request::getData)
                .forEach(d -> d.forEach(req -> BenchTest.execute(TestSuite.client, req, exposePort,
                        (httpResponse, time) -> {
                            TestSuite.responses.get(framework).put(req, httpResponse);
                            final String key = "requestTimeChart" + req.name() + iteration.memory();
                            ((LineChart) TestSuite.report.getCharts()
                                    .computeIfAbsent(key,
                                            k -> new LineChart(key, IntStream.range(1, iteration.max() + 1).mapToObj(String::valueOf).toList(), "Average request time (ms)")))
                                    .save(framework, iteration, req.toString(), time);
                            sleep();
                        })));
    }
}
