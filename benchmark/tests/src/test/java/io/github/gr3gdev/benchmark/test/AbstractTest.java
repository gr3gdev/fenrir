package io.github.gr3gdev.benchmark.test;

import io.github.gr3gdev.bench.BenchTest;
import io.github.gr3gdev.bench.Iteration;
import io.github.gr3gdev.benchmark.TestSuite;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.chart.Chart;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
        System.out.println("Start container database");
        ToStringConsumer logDatabase = new ToStringConsumer();
        database = new DatabaseContainer().withLogConsumer(logDatabase);
        try {
            database.start();
            System.out.println("Container database is started");
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

    private void createChart(Iteration iteration, String title, String key, double measure, String value, String legend, String tooltip) {
        final List<Framework> listFrameworks = Arrays.stream(Framework.values())
                .sorted().toList();
        final int index = listFrameworks.indexOf(getFramework());
        TestSuite.report.getCharts()
                .computeIfAbsent(key,
                        k -> new Chart(key, "charts-css column multiple hide-data show-heading show-labels show-primary-axis show-data-axes show-4-secondary-axes data-spacing-2 datasets-spacing-1", title))
                .getDataset().computeIfAbsent(String.valueOf(iteration.index()), k -> new ArrayList<>())
                .add(index, new Chart.Value(measure, value, legend, tooltip));
    }

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
            final double startedTime = Double.parseDouble(matcher.group(1));
            final String key = "startedTimeChart";
            createChart(iteration, "Started time", key, startedTime, startedTime + "s",
                    getFramework().getName() + " (" + iteration.memory() + "MB)",
                    getFramework().getName() + " started up in " + startedTime + " seconds in iteration #" + iteration.index() + " with " + iteration.memory() + "MB of memory");
        } catch (IllegalStateException e) {
            throw new RuntimeException("Error with pattern [" + pattern + "]\n" + logs);
        }
    }

    public void start(Iteration iteration) {
        System.out.println("Start container " + getFramework().name());
        this.logService = new ToStringConsumer();
        this.service = new FrameworkContainer(getFramework(), iteration.memory())
                .withLogConsumer(logService)
                .withEnv("DATABASE_URL", "jdbc:postgresql://database:5432/benchmark");
        try {
            service.start();
            System.out.println("Container " + getFramework().name() + " is started");
        } catch (Exception exc) {
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
            @IteratorSource.IterationConf(count = 10, memory = 512L),
            @IteratorSource.IterationConf(count = 10, memory = 1000L)
    })
    @DisplayName("Execute benchmark")
    void benchmark(Iteration iteration) throws IOException {
        start(iteration);
        final Framework framework = getFramework();
        final int exposePort = service.getMappedPort(getFramework().getPort());
        final File iterationDirectory = new File(framework.getDirectory(false), iteration.memory() + "_" + iteration.index());
        ;
        BenchTest.load()
                .forEach(request -> {
                    System.out.println("Execute " + request.name());
                    BenchTest.execute(TestSuite.client, request, exposePort,
                            (httpResponse, time, error) -> {
                                Long res = time;
                                if (error != null) {
                                    System.out.println(logService.toString(StandardCharsets.UTF_8));
                                    error.printStackTrace();
                                    res = -1L;
                                }
                                final File requestFile = TestSuite.getRequestFile(framework, request, iteration.index(), iteration.memory());
                                try (final FileOutputStream output = new FileOutputStream(requestFile)) {
                                    Optional.ofNullable(httpResponse)
                                            .ifPresent(
                                                    r -> {
                                                        try (final InputStream input = r.body()) {
                                                            output.write((r.statusCode() + "\n").getBytes(StandardCharsets.UTF_8));
                                                            input.transferTo(output);
                                                        } catch (IOException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                            );
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                final String key = "requestTimeChart" + request.uid();
                                createChart(iteration, "Request time : " + request.name(), key, res.doubleValue(), res + "ms",
                                        getFramework().getName() + " (" + iteration.memory() + "MB)",
                                        "The request " + request.name() + " respond in " + res + " milliseconds at iteration #" + iteration.index() + " with " + iteration.memory() + "MB of memory");
                                sleep();
                            });
                });

        Files.writeString(new File(iterationDirectory, getFramework().name() + ".log").toPath(),
                logService.toString(StandardCharsets.UTF_8));
    }
}
