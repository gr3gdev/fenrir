package io.github.gr3gdev.benchmark.fenrir;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.github.gr3gdev.bench.BenchTest;
import io.github.gr3gdev.bench.data.Request;
import io.github.gr3gdev.fenrir.test.App;
import io.github.gr3gdev.fenrir.test.BeforeApp;
import io.github.gr3gdev.fenrir.test.FenrirExtension;
import io.github.gr3gdev.fenrir.test.TestApplication;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;

@ExtendWith(FenrirExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FenrirAppTest {

    private static final Network NETWORK = Network.newNetwork();
    private static final ToStringConsumer LOG_DATABASE = new ToStringConsumer();

    @App(FenrirApp.class)
    private TestApplication application;

    private static DatabaseContainer database;

    static class DatabaseContainer extends PostgreSQLContainer<DatabaseContainer> {
        DatabaseContainer() {
            super(DockerImageName.parse("postgres:12-alpine"));
            this.withUsername("bench_user");
            this.withPassword("bench_pass");
            this.withDatabaseName("benchmark");
            this.withInitScript("create_database.sql");
            this.withNetwork(NETWORK);
            this.withNetworkAliases("database");
            this.withExposedPorts(5432);
            this.withCreateContainerCmdModifier(
                    cmd -> cmd.withHostConfig(new HostConfig()
                            .withPortBindings(new PortBinding(Ports.Binding.bindPort(5432), new ExposedPort(5432)))));
            this.waitingFor(Wait.forListeningPorts(5432));
        }
    }

    @BeforeApp
    public static void startDatabase() {
        database = new DatabaseContainer().withLogConsumer(LOG_DATABASE);
        try {
            database.start();
        } catch (ContainerLaunchException exc) {
            System.out.println(LOG_DATABASE.toString(StandardCharsets.UTF_8));
            throw exc;
        }
    }

    @AfterAll
    public static void stopDatabase() {
        database.stop();
        System.out.println(LOG_DATABASE.toString(StandardCharsets.UTF_8));
    }

    @BeforeAll
    public static void beforeAll() {
        try {
            LogManager.getLogManager().readConfiguration(FenrirAppTest.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to read logging.properties", e);
        }
    }

    void validate(HttpResponse<String> res, Long time) {
        Assertions.assertTrue(time < 500L);
        System.out.println(res + " in " + time + "ms");
    }

    @Order(1)
    @Test
    public void create() {
        Request.CREATE.getData().forEach(req ->
                BenchTest.execute(application.client(), req, application.port(), this::validate));
    }

    @Order(2)
    @Test
    public void update() {
        Request.UPDATE.getData().forEach(req ->
                BenchTest.execute(application.client(), req, application.port(), this::validate));
    }

    @Order(3)
    @Test
    public void findAll() {
        Request.FIND_ALL.getData().forEach(req ->
                BenchTest.execute(application.client(), req, application.port(), this::validate));
    }

    @Order(4)
    @Test
    public void findById() {
        Request.FIND_BY_ID.getData().forEach(req ->
                BenchTest.execute(application.client(), req, application.port(), this::validate));
    }

    @Order(5)
    @Test
    public void delete() {
        Request.DELETE.getData().forEach(req ->
                BenchTest.execute(application.client(), req, application.port(), this::validate));
    }
}