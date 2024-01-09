package io.github.gr3gdev.moon.server;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import io.github.gr3gdev.moon.server.http.HttpStatus;
import io.github.gr3gdev.moon.server.http.RequestMethod;
import io.github.gr3gdev.moon.server.http.RouteListener;

public class ServerTest {

    @Test
    void run() {
        new Server()
                .process("/", RequestMethod.GET,
                        new RouteListener(HttpStatus.OK, "text/html",
                                "<!DOCTYPE HTML><html><body>This is a test</body></html>"
                                        .getBytes(StandardCharsets.UTF_8)))
                .run();
    }
}
