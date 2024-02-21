package io.github.gr3gdev.benchmark;

import io.github.gr3gdev.bench.data.Request;
import io.github.gr3gdev.benchmark.test.FenrirTest;
import io.github.gr3gdev.benchmark.test.QuarkusTest;
import io.github.gr3gdev.benchmark.test.SpringTest;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;

@Suite
@SelectClasses({
        SpringTest.class,
        QuarkusTest.class,
        FenrirTest.class
})
public class TestSuite {
    public static Report report;
    public static HttpClient client;

    public static File getRequestFile(Framework framework, Request req, int index, long memory) {
        final File iterationDirectory = new File(framework.getDirectory(false), memory + "_" + index);
        if (!iterationDirectory.exists()) {
            try {
                Files.createDirectory(iterationDirectory.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new File(iterationDirectory, req.name()
                .replace(" ", "")
                .replace("?", "")
                .replace("&", "_")
                .replace("/", "_"));
    }
}
