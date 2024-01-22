package io.github.gr3gdev.benchmark;

import io.github.gr3gdev.benchmark.test.FenrirTest;
import io.github.gr3gdev.benchmark.test.QuarkusTest;
import io.github.gr3gdev.benchmark.test.SpringTest;
import io.github.gr3gdev.benchmark.test.data.Framework;
import io.github.gr3gdev.benchmark.test.data.Report;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import java.net.http.HttpClient;
import java.util.Map;

@Suite
@SelectClasses({
        SpringTest.class,
        QuarkusTest.class,
        FenrirTest.class
})
public class TestSuite {
    public static Map<Framework, Report> reports;
    public static HttpClient client;
}
