package io.github.gr3gdev.benchmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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
            final Process process = new ProcessBuilder(command).start();
            process.waitFor();
            return String.join("\n", readOutput(process.getInputStream()));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
