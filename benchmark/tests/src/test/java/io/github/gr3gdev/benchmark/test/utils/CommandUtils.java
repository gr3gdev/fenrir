package io.github.gr3gdev.benchmark.test.utils;

import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandUtils {

    private CommandUtils() {
        // None
    }

    private static List<String> readOutput(InputStream inputStream) throws IOException {
        try (final BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines().toList();
        }
    }

    public static String execute(List<String> command) {
        try {
            final Process process = Runtime.getRuntime().exec(command.toArray(String[]::new));
            process.waitFor(30, TimeUnit.SECONDS);
            final int exitCode = process.exitValue();
            final String error = String.join("\n", readOutput(process.getErrorStream()));
            Assertions.assertEquals(0, exitCode, String.join(" ", command) + " : " + error);
            return String.join("\n", readOutput(process.getInputStream()));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
